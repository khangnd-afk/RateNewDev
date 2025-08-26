package com.tnt.rate.ui

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.tnt.rate.R
import com.tnt.rate.core.RateCallback
import com.tnt.rate.core.setBackgroundSafe
import com.tnt.rate.core.setTextColorSafe
import com.tnt.rate.core.setTextSafe
import com.tnt.rate.model.FeedbackReason
import com.tnt.rate.model.OptionButtonConfig

class RateDialogFeedBack(
    context: Context,
    val layoutRes: Int,
    val layoutItemFeedBack: Int,
    val feedbackReasons: List<FeedbackReason>,
    val btnConfig: OptionButtonConfig?,
    val callback: RateCallback
) : BaseDialog(context, layoutRes) {

    private var rcvReason: RecyclerView? = null
    private var edtFeedback: EditText? = null
    private var btnFeedback: TextView? = null
    private var ivClose: View? = null
    private var adapter: ReasonAdapter? = null
    private val reason
        get() = adapter?.currentList?.getOrNull(adapter?.selectedPosition ?: 0)
    private val textFeedback
        get() = if (reason?.requireInput == true) edtFeedback?.text?.trim().toString() else ""

    override fun initView(view: View) {
        rcvReason = view.findViewById(R.id.rcvReason)
        edtFeedback = view.findViewById(R.id.edtFeedback)
        btnFeedback = view.findViewById(R.id.btnFeedback)
        ivClose = view.findViewById(R.id.ivClose)
        initBtnFeedback(false)
        initReasons()
    }

    override fun initListener() {
        edtFeedback?.addTextChangedListener {
            updateBtnState(reason)
        }
        btnFeedback?.setOnClickListener {
            callback.onFeedBack(
                context.getString(reason!!.title),
                textFeedback,
                true
            )
            dismiss()
        }
        ivClose?.setOnClickListener {
            dismiss()
        }
    }

    private fun initReasons() {
        adapter = ReasonAdapter(context, layoutItemFeedBack) { reason ->
            edtFeedback?.visibility = if (reason.requireInput) View.VISIBLE else View.GONE
            updateBtnState(reason)
        }
        rcvReason?.adapter = adapter
        adapter?.submitList(feedbackReasons.toList())
    }

    private fun updateBtnState(reason: FeedbackReason?) {
        reason?.let {
            val hasSelection = adapter?.selectedPosition != RecyclerView.NO_POSITION
            val needInput = reason.requireInput
            val hasText = textFeedback.isNotBlank()
            callback.onFeedBack(
                context.getString(reason.title),
                textFeedback,
                false
            )
            initBtnFeedback(
                (hasSelection && !needInput) || (needInput && hasText)
            )
        }
    }

    private fun initBtnFeedback(isEnable: Boolean) {
        btnFeedback?.isEnabled = isEnable
        if (btnConfig?.backgroundUnselected == null) {
            btnFeedback?.alpha = if (isEnable) 1f else 0.7f
        }

        if (isEnable) {
            btnFeedback?.setBackgroundSafe(btnConfig?.backgroundSelected)
            btnFeedback?.setTextColorSafe(btnConfig?.textColorSelected)
            btnFeedback?.setTextSafe(btnConfig?.textSelected)
        } else {
            btnFeedback?.setBackgroundSafe(btnConfig?.backgroundUnselected)
            btnFeedback?.setTextColorSafe(btnConfig?.textColorUnselected)
            btnFeedback?.setTextSafe(btnConfig?.textUnselected)
        }
    }

    override fun show() {
        super.show()
        runCatching {
            callback.onShowDialog(this, rootView)
        }
    }

    override fun dismiss() {
        super.dismiss()
        callback.onDismissDialog(false)
    }
}