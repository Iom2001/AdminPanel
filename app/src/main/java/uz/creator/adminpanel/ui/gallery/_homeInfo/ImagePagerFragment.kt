package uz.creator.adminpanel.ui.gallery._homeInfo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.synnapps.carouselview.ImageListener
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentImagePagerBinding
import uz.creator.adminpanel.utils.MyDialog
import android.graphics.Bitmap
import uz.creator.adminpanel.utils.showToast
import android.graphics.drawable.Drawable
import android.os.Build
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import com.bumptech.glide.request.target.CustomTarget
import uz.creator.adminpanel.utils.Permanent
import java.io.*
import com.bumptech.glide.request.transition.Transition


class ImagePagerFragment : Fragment() {

    private var _binding: FragmentImagePagerBinding? = null
    private val binding get() = _binding!!
    private lateinit var date: String
    private lateinit var phoneNumber: String
    private var imagePosition: Int = 0
    private var imageUriList = ArrayList<Uri>()
    private lateinit var myDialog: MyDialog
    private val channelID = "uz.creator.adminpanel.ui.gallery._homeInfo1"
    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString("date")?.let { d ->
                date = d
            }
            it.getString("phoneNumber")?.let { p ->
                phoneNumber = p
            }
            it.getInt("position").let { position ->
                imagePosition = position
            }
        }
        myDialog = MyDialog(requireContext())
        imageUriList.addAll(Permanent.imageUriList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagePagerBinding.inflate(inflater, container, false)
        binding.carouselView.setImageListener(imageListener)
        binding.carouselView.pageCount = imageUriList.size
        binding.carouselView.currentItem = imagePosition

        notificationManager =
            requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelID, "DemoChannel", "this is a demo")

        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.downloadImg.setOnClickListener {
            myDialog.showDialog()
            runBlocking {
                downloadAndSaveImage()
            }
        }

        binding.callBtn.setOnClickListener {
            call()
        }

        return binding.root
    }

    private fun displayNotification(bitmap: Bitmap) {
        val notificationId = 45
        var notification =
            NotificationCompat.Builder(requireContext(), channelID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Downloaded Image")
                .setContentText("In External Storage Pictures/Dilshot Rs")
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null)
                )
                .build()
        notificationManager?.notify(notificationId, notification)

    }

    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private suspend fun downloadAndSaveImage() {
        coroutineScope {
            val currentPosition = binding.carouselView.currentItem
            Glide.with(requireContext())
                .asBitmap()
                .load(imageUriList[currentPosition])
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                                val resolver = context?.contentResolver
                                val contentValues = ContentValues().apply {
                                    put(
                                        MediaStore.MediaColumns.DISPLAY_NAME,
                                        "${phoneNumber}${date}/${currentPosition}.JPEG"
                                    )
                                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                    put(
                                        MediaStore.MediaColumns.RELATIVE_PATH,
                                        "Pictures/Dilshot Rs"
                                    )
                                }

                                val uri = resolver?.insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    contentValues
                                )

                                if (uri != null) {
                                    resolver.openOutputStream(uri).use { fOut ->
                                        try {
                                            resource.compress(
                                                Bitmap.CompressFormat.JPEG,
                                                100,
                                                fOut
                                            )
                                            fOut?.close()
                                            displayNotification(resource)
                                            myDialog.dismissDialog()
                                            requireContext().showToast("Downloaded Successfully")
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            myDialog.dismissDialog()
                                            requireContext().showToast("Failed")
                                        }
                                    }
                                } else {
                                    myDialog.dismissDialog()
                                    requireContext().showToast("Failed")
                                }
                            } else {
                                val imageFileName = "${phoneNumber}${date}/${currentPosition}.jpg"
                                val storageDir = File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                        .toString() + "Dilshot Rs"
                                )
                                var success = true
                                if (!storageDir.exists()) {
                                    success = storageDir.mkdirs()
                                }
                                if (success) {
                                    val imageFile = File(storageDir, imageFileName)
//                    imageFile.absolutePath
                                    try {
                                        val fOut: OutputStream = FileOutputStream(imageFile)
                                        resource.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                                        fOut.close()
                                        displayNotification(resource)
                                        myDialog.dismissDialog()
                                        requireContext().showToast("Downloaded Successfully")
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        myDialog.dismissDialog()
                                        requireContext().showToast("Failed")
                                    }
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            myDialog.dismissDialog()
                            requireContext().showToast("Failed")
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
        }
    }

    private fun call() {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        val phone =
            phoneNumber.substring(0, 4) + phoneNumber.substring(
                6,
                8
            ) + phoneNumber.substring(10, 14) + phoneNumber.substring(
                14,
                17
            ) + phoneNumber.substring(17)
        dialIntent.data = Uri.parse("tel:$phone")
        startActivity(dialIntent)
    }

    private val imageListener = ImageListener { position, imageView ->
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        Glide.with(requireContext()).load(imageUriList[position])
            .placeholder(R.drawable.home_placeholder)
            .error(R.drawable.errorplaceholder).into(imageView)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}