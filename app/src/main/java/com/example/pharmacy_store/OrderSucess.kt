package com.example.pharmacy_store

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pharmacy_store.Models.ItemModel
import com.example.pharmacy_store.Models.OrderModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class OrderSucess : AppCompatActivity() {

    lateinit var documentId: String
    lateinit var totalPrice: String

    lateinit var uid: String
    lateinit var address: String
    lateinit var city: String
    lateinit var postalCode: String
    lateinit var name: String
    lateinit var items: List<ItemModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_sucess)

        documentId = intent.getStringExtra("documentId").toString()
        totalPrice = intent.getStringExtra("totalPrice").toString()


        val referenceNumValueTextView = findViewById<TextView>(R.id.refernceNumValue)
        val totalPayValueTextView = findViewById<TextView>(R.id.totalpayValue)

        referenceNumValueTextView.text = documentId
        totalPayValueTextView.text = "Rs.${totalPrice}"



        val buttonGoHome = findViewById<Button>(R.id.buttongohome)
        buttonGoHome.setOnClickListener {

            val intent = Intent(this, UserLandingPage::class.java)
            startActivity(intent)


        }


        val buttonPdf = findViewById<Button>(R.id.buttondownloadpdf)
        buttonPdf.setOnClickListener {
            generatePdf()

        }

        val db = FirebaseFirestore.getInstance()
        val ordersCollection = db.collection("orders")
        val orderDocument = ordersCollection.document(documentId)

        orderDocument.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot: DocumentSnapshot? = task.result
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val orderModel: OrderModel? = documentSnapshot.toObject(OrderModel::class.java)


                    uid = orderModel?.uid ?: ""
                    address = orderModel?.address ?: ""
                    city = orderModel?.city ?: ""
                    postalCode = orderModel?.postalcode ?: ""
                    name = orderModel?.name ?: ""
                    items = orderModel?.items ?: emptyList()

                }
            } else {
                Log.d(TAG, "Error getting document: ", task.exception)
            }
        }

    }

    private fun generatePdf() {
        val document = Document(PageSize.A4)
        val pdfFileName = "order_receipt.pdf"

        try {
            val pdfFilePath = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/$pdfFileName"
            PdfWriter.getInstance(document, FileOutputStream(pdfFilePath))
            document.open()

            // Add title with larger font and centered alignment
            val titleFont = Font(BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED), 24f, Font.NORMAL)
            val title = Paragraph("Order Receipt", titleFont)
            title.alignment = Element.ALIGN_CENTER
            document.add(title)

            // Add a separator line
            val separator = Chunk("                                               _______________________________")
            document.add(separator)

            // Add order details with bold font and increased spacing
            val normalFont = Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED), 12f, Font.NORMAL)
            val orderDetails = Paragraph()
            orderDetails.spacingAfter = 10f
            orderDetails.add(Chunk("\nReference Number: $documentId\n\n", normalFont))
            orderDetails.add(Chunk("Total Pay: Rs.$totalPrice\n\n", normalFont))
            orderDetails.add(Chunk("Name: $name\n", normalFont))
            orderDetails.add(Chunk("Address: $address\n", normalFont))
            orderDetails.add(Chunk("City: $city\n", normalFont))
            orderDetails.add(Chunk("Postal Code: $postalCode\n", normalFont))

            document.add(orderDetails)

            // Add items with a table
            val table = PdfPTable(2)
            table.widthPercentage = 100f
            table.spacingAfter = 10f
            table.addCell(createCell("Item", normalFont, true))
            table.addCell(createCell("Quantity", normalFont, true))

            for (item in items) {
                table.addCell(createCell(item.itemName, normalFont))
                table.addCell(createCell(item.quantity.toString(), normalFont))
            }
            document.add(table)

            // Close the document
            document.close()

            Toast.makeText(this, "PDF generated and saved at $pdfFilePath", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createCell(content: String, font: Font, isHeader: Boolean = false): PdfPCell {
        val cell = PdfPCell(Paragraph(content, font))
        cell.setPadding(5f) // Set padding using setPadding() method
        if (isHeader) {
            cell.backgroundColor = BaseColor.LIGHT_GRAY
            cell.horizontalAlignment = Element.ALIGN_CENTER
        }
        return cell
    }







}
