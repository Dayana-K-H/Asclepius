package com.dicoding.asclepius.view.analyze

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.PredictionEntity
import com.dicoding.asclepius.databinding.FragmentAnalyzeBinding
import com.dicoding.asclepius.di.Injection
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.history.HistoryActivity
import com.dicoding.asclepius.view.PredictionViewModel
import com.dicoding.asclepius.view.result.ResultActivity
import com.dicoding.asclepius.view.ViewModelFactory
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat

class AnalyzeFragment : Fragment() {

    private lateinit var binding: FragmentAnalyzeBinding

    private val analyzeViewModel by viewModels<AnalyzeViewModel>()

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private lateinit var predictionViewModel: PredictionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.history.setOnClickListener {
            val intent = Intent(activity, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            analyzeViewModel.imageUri.value?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }

        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    if (results.isNullOrEmpty()) {
                        showToast("Tidak ada hasil analisis.")
                        return
                    }

                    val category = results[0].categories.sortedByDescending { it.score }[0]
                    val result = "${category.label} ${NumberFormat.getPercentInstance().format(category.score)}"

                    analyzeViewModel.imageUri.value?.let {
                        moveToResult(result, it)
                        addHistory(result, it)}
                }

                override fun onError(error: String) {
                    showToast("Error: $error")
                }
            }
        )

        val repository = Injection.provideRepository(requireContext())
        val factory = ViewModelFactory(repository)
        predictionViewModel = ViewModelProvider(this, factory)[PredictionViewModel::class.java]

        analyzeViewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let { showImage(it) }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            startCrop(it)
        } ?: run {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCrop(uri: Uri) {
        val fileName = "croppedImage_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, fileName))
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(224, 224)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                analyzeViewModel.setImageUri(resultUri)
                showImage(resultUri)
            } else {
                showToast("Gagal memuat gambar yang di-crop.")
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.let { showToast("Crop error: ${it.message}") }
        }
    }

    private fun showImage(uri: Uri) {
            binding.previewImageView.setImageURI(null)
            binding.previewImageView.invalidate()
            binding.previewImageView.setImageURI(uri)
    }

    private fun analyzeImage(uri: Uri) {
        imageClassifierHelper.classifyStaticImage(uri)
    }

    private fun addHistory(result: String, uri: Uri) {
        val history = PredictionEntity(
            imageUri = uri.toString(),
            predictionResult = result,
        )
        Log.d("AddHistory", "Adding to history: $history")
        predictionViewModel.insertPrediction(history)
    }

    private fun moveToResult(result: String, uri: Uri) {
        val intent = Intent(activity, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
            putExtra(ResultActivity.EXTRA_RESULT, result)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
