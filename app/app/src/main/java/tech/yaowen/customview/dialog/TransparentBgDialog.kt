package tech.yaowen.customview.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import tech.yaowen.customview.R

class TransparentBgDialog: DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, tech.yaowen.theme.R.style.MyDialog);
    }
}