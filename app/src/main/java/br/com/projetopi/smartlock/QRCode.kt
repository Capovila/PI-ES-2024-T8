package br.com.projetopi.smartlock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class QRCode : Fragment() {
    private lateinit var qrCode: ImageView
    private lateinit var locacaoID: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_q_r_code, container, false)

        qrCode = root.findViewById(R.id.qrcode)

        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode("ola", BarcodeFormat.QR_CODE, 300, 300)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)

        qrCode.setImageBitmap(bitmap)

        return root
    }
}