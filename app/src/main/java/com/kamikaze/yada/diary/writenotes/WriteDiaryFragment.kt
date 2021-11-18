package com.kamikaze.yada.diary.writenotes

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Layout
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kamikaze.yada.R
import com.kamikaze.yada.dao.NotesDao
import com.kamikaze.yada.databinding.FragmentWriteDiaryBinding
import com.kamikaze.yada.diary.DiaryHandler
import com.kamikaze.yada.model.Notes
import io.grpc.Context
import java.io.File
import kotlin.math.floor


class WriteDiaryFragment : Fragment(R.layout.fragment_write_diary) {
    private var _binding: FragmentWriteDiaryBinding? = null
    private val binding get() = _binding!!
    val storageref = FirebaseStorage.getInstance().reference
    lateinit var mlayout: GridLayout
     private var layoutmisc : LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWriteDiaryBinding.inflate(inflater, container, false)
        val view = binding.root
        val topAppBar = binding.topAppBar
        val writeET = binding.edithere
        writeET.visibility = View.GONE
        val seeTV = binding.seehere
        val title = binding.title
        val act = activity as WriteActivity
        topAppBar.title = act.title
        val fab = binding.fab
        val imgview = ImageView(act)




        initMisc(act.findViewById(R.id.layoutmiscnote))


        //-----------------------------------------------------------
        mlayout = view.findViewById(R.id.layout)

        fab.setOnClickListener{
            if (ActivityCompat.checkSelfPermission(act,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),2000)
            }
            else{
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent , "Select Picture"),1000)
            }
    }
        //-------------------------------------------------------------
        val nd = NotesDao()
       nd.setNote(seeTV,act.position,writeET , title , mlayout)
        seeTV.movementMethod = ScrollingMovementMethod()
        topAppBar.setOnMenuItemClickListener { menuItem -> when (menuItem.itemId){
            R.id.favorite ->{
                writeET.visibility = View.GONE
                seeTV.text = writeET.text
                seeTV.visibility = View.VISIBLE
                val note :Notes = Notes(title.text.toString(),"Random","Random0",writeET.text.toString() , )
                val act = activity as com.kamikaze.yada.diary.writenotes.WriteActivity
                var diaryins:DiaryHandler = DiaryHandler(activity)
                diaryins.updateDiary(act.position, note)
                true
            }
            R.id.edit ->{
                seeTV.visibility = View.GONE
                writeET.setText(seeTV.text.toString())
                writeET.visibility = View.VISIBLE
                true
            }
            else   -> false
        } }
        return view
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun addView(imageView : ImageView, width : Int, height : Int){

       val layoutParams = GridLayout.LayoutParams()



       layoutParams.setMargins(0,1,0,1)



        imageView.layoutParams = layoutParams
        mlayout.addView(imageView)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode==RESULT_OK){
            if(requestCode==1000){

                val returnUri = data?.data
                val act = activity as com.kamikaze.yada.diary.writenotes.WriteActivity

                val bitmapImage = MediaStore.Images.Media.getBitmap(act.contentResolver,returnUri)
                val imgview = ImageView(act)
                imgview.setImageBitmap(bitmapImage)
                //local
                addView(imgview , LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)



            }
        }
    }

    private  fun initMisc(layoutmisc : LinearLayout?) {
        val act = activity as WriteActivity
        if (layoutmisc!=null){
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutmisc)
        layoutmisc.setOnClickListener{
            if(bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED ){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
    }}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}