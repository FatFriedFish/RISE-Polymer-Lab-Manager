package edu.sefs.risepolylab.ui.editinv

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import edu.sefs.risepolylab.MainViewModel
import edu.sefs.risepolylab.databinding.FragmentEditinventoryBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.CharacterSetECI
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*

class EditInvFragment : Fragment() {

    private var _binding: FragmentEditinventoryBinding? = null
    private val viewModel : MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val resultLauncher1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanned = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
            binding.editScanChemical.text = scanned.contents
        }else{
            Snackbar.make(requireView(), "User cancelled scan", Snackbar.LENGTH_SHORT).show()
        }
    }
    private val resultLauncher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanned = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
            viewModel.refreshChemical(scanned.contents){
                Snackbar.make(requireView(), "Refresh chemical last date successful", Snackbar.LENGTH_SHORT).show()
            }
        }else{
            Snackbar.make(requireView(), "User cancelled scan", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditinventoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //add chemical
        binding.editAddChemical.setOnClickListener {
            if (binding.editChemicalName.text.toString() == "" ||
                binding.editChemicalCAS.text.toString() == "" ||
                binding.editChemicalLocation.text.toString() == "" ||
                binding.editContentSize.text.toString() == ""){
                Snackbar.make(it, "You must fill in all fields", Snackbar.LENGTH_SHORT).show()
            }else{
                viewModel.addChemical(
                    binding.editChemicalName.text.toString(),
                    binding.editChemicalCAS.text.toString(),
                    binding.editChemicalLocation.text.toString(),
                    binding.editContentSize.text.toString()){qrText ->
                        binding.editQRtext.text = qrText
                        binding.editQRimgae.setImageBitmap(generateQR(qrText))
                    }
            }
        }
        //refresh inventory
        binding.editCheckChemical.setOnClickListener {
            startQRCodeScanner(resultLauncher2)
        }

        //scan chemical and delete
        binding.editScanChemical.setOnClickListener {
            startQRCodeScanner(resultLauncher1)
        }
        binding.editDeleteButton.setOnClickListener {
            viewModel.removeChemical(binding.editScanChemical.text.toString()){
                Snackbar.make(it, "Delete chemical susccessful", Snackbar.LENGTH_SHORT).show()
            }
            viewModel.fetchAllChemicalMetaList()
        }
        

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Throws(WriterException::class)
    fun generateQR(value: String) : Bitmap? {
        val bitMatrix: BitMatrix
        try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            hints[EncodeHintType.CHARACTER_SET] = CharacterSetECI.UTF8

            bitMatrix = MultiFormatWriter().encode(
                value,
                BarcodeFormat.QR_CODE,
                500,
                500,
                hints
            )
        }catch (Illegalargumentexception : IllegalArgumentException){
            return null
        }

        val bitMatrixWidth = bitMatrix.width

        val bitMatrixHeight = bitMatrix.height

        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth

            for (x in 0 until bitMatrixWidth) {

                pixels[offset + x] = if (bitMatrix.get(x, y))
                    Color.BLACK
                else
                    Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)

        return bitmap
    }

    fun startQRCodeScanner(resultLauncher: ActivityResultLauncher<Intent>) {
        val integrator = IntentIntegrator(requireActivity())
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setBeepEnabled(false)
        integrator.setOrientationLocked(true)
        resultLauncher.launch(integrator.createScanIntent())
    }

}