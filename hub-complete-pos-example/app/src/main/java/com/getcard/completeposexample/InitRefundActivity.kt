package com.getcard.completeposexample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getcard.completeposexample.database.HubDatabase
import com.getcard.completeposexample.database.models.TransactionsModel
import com.getcard.completeposexample.databinding.ActivityInitRefundBinding
import com.getcard.completeposexample.databinding.TransactionRowItemBinding
import com.getcard.hubinterface.OperationStatus
import com.getcard.hubinterface.authentication.AuthParams
import com.getcard.hubinterface.transaction.InstallmentType
import com.getcard.hubinterface.transaction.PaymentType
import com.getcard.hubinterface.transaction.TransactionParams
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ItemAdapter(
    private val items: List<TransactionsModel>,
    private val onRefundClick: (TransactionsModel) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: TransactionRowItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = TransactionRowItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int) {

        val item = items[position]
        viewHolder.binding.amount.text = Utils.applyMoneyMask(item.amount)
        viewHolder.binding.paymentType.text = when (item.paymentType) {
            PaymentType.CREDIT -> "Crédito"
            PaymentType.DEBIT -> "Débito"
        }
        viewHolder.binding.installmentType.text = when (item.installmentType) {
            InstallmentType.INSTALLMENTS -> "Parcelado"
            InstallmentType.ONE_TIME -> "À Vista"
        }

        if (item.installmentType == InstallmentType.INSTALLMENTS) {
            viewHolder.binding.installments.text = "${item.installmentNumber} parcelas"
        } else {
            viewHolder.binding.installments.isVisible = false
        }

        if (item.timestamp != null)
            viewHolder.binding.dateTime.text = Utils.formatTimestamp(item.timestamp)

        viewHolder.binding.refundButton.isEnabled = false
        viewHolder.binding.status.text = when (item.status) {
            OperationStatus.SUCCESS -> {
                if (item.isRefunded) {
                    viewHolder.binding.status.setTextColor(Color.RED)
                    "Estornada"
                } else {
                    viewHolder.binding.status.setTextColor(Color.GREEN)
                    viewHolder.binding.refundButton.isEnabled = true
                    viewHolder.binding.refundButton.setOnClickListener { onRefundClick(item) }
                    "Não estornada"
                }
            }

            OperationStatus.FAILED -> "Erro"
            OperationStatus.CANCELLED -> "Cancelada"
            OperationStatus.DECLINED -> "Rejeitada"
            OperationStatus.UNKNOWN -> "Status Desconhecido"
        }

        viewHolder.binding.provider.text = item.paymentProviderType.name
    }

    override fun getItemCount() = items.size
}

class InitRefundActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "InitRefundActivity"
    }

    private lateinit var binding: ActivityInitRefundBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInitRefundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            finish()
        }

        val database = HubDatabase.getInstance(this)
        val hubSettingsDAO = database.settingsDao()


        lifecycleScope.launch {
            var token = hubSettingsDAO.findFirst()?.token.toString()

            val transactionsList =
                HubDatabase.getInstance(this@InitRefundActivity).transactionsDao().findAll()

            val adapter = ItemAdapter(transactionsList.requireNoNulls()) { transaction ->
                val refundIntent = Intent(this@InitRefundActivity, RefundActivity::class.java)
                refundIntent.putExtra("TRANSACTION_ID", transaction.id)
                refundIntent.putExtra(
                    "REFUND_PARAMS",
                    TransactionParams(
                        amount = BigDecimal(transaction.amount),
                        paymentType = transaction.paymentType,
                        nsuHost = transaction.nsuHost,
                        transactionTimestamp = transaction.timestamp,
                        refund = true,
                    )
                )
                refundIntent.putExtra(
                    "AUTH_PARAMS",
                    AuthParams(
                        "80345267000150",
                        token
                    )
                )
                launcher.launch(refundIntent)
            }

            binding.recyclerView.layoutManager = LinearLayoutManager(this@InitRefundActivity)
            binding.recyclerView.addItemDecoration(
                DividerItemDecoration(
                    this@InitRefundActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            binding.recyclerView.adapter = adapter
        }
    }
}
