package com.oguncan.shareapp.Fragments

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.oguncan.shareapp.R
import com.oguncan.shareapp.utils.EventBusDataEvents
import com.oguncan.shareapp.utils.FileOperations
import com.oguncan.shareapp.utils.ShareActivityGridViewAdapter
import com.oguncan.shareapp.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.fragment_add_post.*
import kotlinx.android.synthetic.main.fragment_add_post.view.*
import org.greenrobot.eventbus.EventBus

/**
 * A simple [Fragment] subclass.
 */
class AddPostFragment : Fragment() {
    var imagePath : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_add_post, container, false)
        var filesPaths = ArrayList<String>()

        var root = Environment.getExternalStorageDirectory().path
        var cameraPhotos = root + "/DCIM/Camera"
        var screenshotsPhotos = root + "/Pictures/Screenshots"
        var whatsAppPhotos = root + "/WhatsApp/Media/WhatsApp Images"

        filesPaths.add(cameraPhotos)
        filesPaths.add(screenshotsPhotos)
        filesPaths.add(whatsAppPhotos)

        var filesNames = ArrayList<String>()
        filesNames.add("Kamera")
        filesNames.add("Ekran Görüntüleri")
        filesNames.add("WhatsApp Resimleri")

        var spinnerArrayAdapter =
            ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, filesNames)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        view.spinnerFileNames.adapter = spinnerArrayAdapter

        view.spinnerFileNames.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setupGridView(FileOperations.getFilesInFolder(filesPaths.get(p2)))
            }

        }
        view.imgShareGalleryClose.setOnClickListener {
            activity?.onBackPressed()
        }

        view.txtShareGalleryNextButton.setOnClickListener {

            activity!!.shareLayoutRoot.visibility = View.GONE
            activity!!.shareLayoutContainer.visibility = View.VISIBLE
            var transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.shareLayoutContainer, PostNextFragment())
            transaction.addToBackStack("shareNextFragment")
            transaction.commit()
            EventBus.getDefault()
                .postSticky(EventBusDataEvents.SendShareImage(Uri.parse(imagePath)))
        }
        return view
    }

    fun setupGridView(allFilesInFolder : ArrayList<String>){
        var gridViewAdapter = ShareActivityGridViewAdapter(activity!!, R.layout.share_gridview_photo, allFilesInFolder)
        gridViewShareGallery.adapter = gridViewAdapter
        imagePath = allFilesInFolder.get(0)
        showImageOrView(allFilesInFolder.get(0))
        gridViewShareGallery.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                showImageOrView(allFilesInFolder.get(p2))
            }

        })
    }

    fun showImageOrView(imageOrVideoPath : String){
        var fileType = imageOrVideoPath.substring(imageOrVideoPath.lastIndexOf("."))
        //file://shareapp.mp4 or file://shareapp.jpg
//        if(fileType != null && fileType.equals(".mp4")){
//            shareUniversalVideoView.visibility = View.VISIBLE
//            shareImageCropView.visibility = View.GONE
//            shareUniversalVideoView.setVideoURI(Uri.parse(imageOrVideoPath))
//            shareUniversalVideoView.start()
//
//        }
        if(fileType != null && (fileType.equals(".png") || fileType.equals(".jpeg") || fileType.equals(".jpg"))){
            shareUniversalVideoView.visibility = View.GONE
            shareImageCropView.visibility = View.VISIBLE
            UniversalImageLoader.setImage("file:/"+imageOrVideoPath, shareImageCropView, null,"")

        }
    }



}
