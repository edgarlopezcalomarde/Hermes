package com.edloca.hermes.ui.chat


import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edloca.hermes.R
import com.edloca.hermes.data.models.MessageModel
import com.edloca.hermes.databinding.ActivityChatBinding
import com.edloca.hermes.ui.chatlist.ChatListActivity
import com.edloca.hermes.utils.toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class ChatActivity : AppCompatActivity(), RecyclerPhothos.ItemListener,
    RecyclerMessage.OnUserClickImage {

    private lateinit var binding: ActivityChatBinding

    private lateinit var rvChat: RecyclerView
    private lateinit var messageAdapter: RecyclerMessage

    var messageModelList: MutableList<MessageModel> = mutableListOf()


    //Gallery PHothos
    private lateinit var rvGalleryPhotos: RecyclerView
    private lateinit var photosAdapter: RecyclerPhothos
    private var imagesList:MutableList<Uri> = ArrayList()

    private val chatViewModel:ChatViewModel by viewModels()

    private var responseLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode== Activity.RESULT_OK){

            if (it.data != null){
                if (it.data?.clipData?.itemCount != null){
                    for (i in 0 until it.data?.clipData?.itemCount!!){
                        imagesList.add(it.data?.clipData?.getItemAt(i)?.uri!!)
                    }
                }else{
                    imagesList.add(it.data?.data!!)
                }

                chatViewModel.imageList.postValue(imagesList)
            }



        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.modern_blue)

        chatViewModel.setRooms(intent.getStringExtra("reciverid")!!)
        chatViewModel.getInfoUser(intent.getStringExtra("reciverid")!!)

        chatViewModel.fetchMessagesList(messageModelList)

        setUpPhotosAdapter()
        setUpMessageAdapter()

        chatViewModel.messageData.observe(this, Observer {
            messageAdapter.setData(it)
            messageAdapter.notifyDataSetChanged()
            rvChat.scrollToPosition(it.size-1)
        })




        chatViewModel.isStorageTaskComplete.observe(this, Observer {
            if (it) {
                imagesList.clear()
                photosAdapter.notifyDataSetChanged()
            }
        })


        binding.btnSend.setOnClickListener {

            if (binding.etMessage.text.toString().isNotEmpty()){
                chatViewModel.sendMessage(binding.etMessage.text.toString())
                binding.etMessage.text.clear()

            }


            if (imagesList.isNotEmpty()){
                chatViewModel.sendImage(imagesList)
            }

        }


        chatViewModel.userInfo.observe(this, Observer {

            it.username?.replaceFirstChar {char -> char.uppercaseChar() }

            binding.tvNameContact.text = it.username!![0].uppercaseChar() + it.username.substring(1)
            Glide.with(this)
                .load(it.imgUrl)
                .circleCrop()
                .placeholder(R.drawable.account_circle)
                .into(binding.profileImage)

        })

        chatViewModel.imageList.observe(this, Observer {
            photosAdapter.setlistData(it)
            photosAdapter.notifyDataSetChanged()
        })


        binding.btnGetPhothoFromGallery.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 101)

            }else{
                launchIntent()
            }
        }


    }

    private fun setUpMessageAdapter() {
        messageAdapter = RecyclerMessage(this ,chatViewModel.chatId(),this)
        rvChat = binding.rvChat
        rvChat.setHasFixedSize(false)
        rvChat.layoutManager =  LinearLayoutManager(this)
        rvChat.adapter = messageAdapter
    }

    private fun setUpPhotosAdapter() {
        photosAdapter = RecyclerPhothos(this ,this)
        rvGalleryPhotos = binding.rvGalleryPhotos
        rvGalleryPhotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvGalleryPhotos.adapter = photosAdapter
    }



    override fun onBackPressed() {
        startActivity(Intent(this@ChatActivity, ChatListActivity::class.java))
        finish()
    }

    private fun launchIntent(){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        responseLauncher.launch(Intent.createChooser(intent, "Gallery"))
    }

    override fun onDeleteItemClick(positionItem: Int) {

        imagesList.removeAt(positionItem)
        photosAdapter.notifyDataSetChanged()
    }

    override fun onImageClick(img: ImageView) {
        if (ContextCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this@ChatActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 123)
            ActivityCompat.requestPermissions(this@ChatActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 123)

            Toast.makeText(this,"You need to provide permissions to access the storage", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this,"Downloading....", Toast.LENGTH_SHORT).show()

            var image: Uri
            var contentResolver: ContentResolver = contentResolver


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                image = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }else{
                image = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            var contentValue: ContentValues = ContentValues()

            contentValue.put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
            contentValue.put(MediaStore.Images.Media.MIME_TYPE, "images/*")
            var uri = contentResolver.insert(image, contentValue)


            try {


                val bitmapDrawable = img.drawable
                val bitmap = bitmapDrawable.toBitmap()

                if (uri!=null){
                    val outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri))
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    Objects.requireNonNull(outputStream)

                    Toast.makeText(this@ChatActivity,"Image Saved Successfully", Toast.LENGTH_SHORT).show()
                }


            }catch (e: Exception){

            }




        }


    }






}