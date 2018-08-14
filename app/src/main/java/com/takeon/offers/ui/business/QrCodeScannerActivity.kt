package com.takeon.offers.ui.business

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import com.google.android.gms.vision.barcode.Barcode
import com.takeon.offers.R
import com.takeon.offers.utils.CommonDataUtility
import info.androidhive.barcode.BarcodeReader
import kotlinx.android.synthetic.main.takeon_activity_scanner.btnApply
import kotlinx.android.synthetic.main.takeon_activity_scanner.edtPin
import kotlinx.android.synthetic.main.takeon_activity_scanner.llRoot

class QrCodeScannerActivity : AppCompatActivity(), BarcodeReader.BarcodeReaderListener {

  private var barcodeReader: BarcodeReader? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.takeon_activity_scanner)

    // getting barcode instance
    barcodeReader = supportFragmentManager.findFragmentById(R.id.barcode_fragment) as BarcodeReader

    /*
     * Providing beep sound. The sound file has to be placed in
     * `assets` folder
     */
//    barcodeReader!!.setBeepSoundFile("shutter.mp3")

    btnApply.setOnClickListener {

      if (edtPin.text.toString().trim().length < 4) {
        CommonDataUtility.showSnackBar(llRoot, "Please enter 4 digit pin")
      } else {
        val intent = Intent()
        intent.putExtra("pin", edtPin.text.toString().trim())
        setResult(Activity.RESULT_OK, intent)
        finish()
      }
    }
  }

  override fun onScanned(barcode: Barcode) {
    Log.e(TAG, "onScanned: " + barcode.displayValue)
    barcodeReader!!.playBeep()

    runOnUiThread {
      val intent = Intent()
      intent.putExtra("pin", barcode.displayValue)
      setResult(Activity.RESULT_OK, intent)
      finish()
    }
  }

  override fun onScannedMultiple(barcodes: List<Barcode>) {
    Log.e(TAG, "onScannedMultiple: " + barcodes.size)

    var codes = ""
    for (barcode in barcodes) {
      codes += barcode.displayValue + ", "
    }

    val finalCodes = codes
    runOnUiThread {
      Toast.makeText(applicationContext, "Barcodes: $finalCodes", Toast.LENGTH_SHORT)
          .show()
    }
  }

  override fun onBitmapScanned(sparseArray: SparseArray<Barcode>) {

  }

  override fun onScanError(errorMessage: String) {

  }

  override fun onCameraPermissionDenied() {
    Toast.makeText(applicationContext, "Camera permission denied!", Toast.LENGTH_LONG)
        .show()
    finish()
  }

  companion object {
    private val TAG = QrCodeScannerActivity::class.java.simpleName
  }
}