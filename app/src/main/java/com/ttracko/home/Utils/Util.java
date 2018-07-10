package com.ttracko.home.Utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ttracko.R;
import com.ttracko.home.interfaces.MobileDialogListner;

/**
 * Created by root on 28/6/18.
 */

public class Util {
    public static boolean validateIsMobile(String mobieNo) {
        if (TextUtils.isEmpty(mobieNo)) {
            return false;
        }
        if (mobieNo != null && mobieNo.length() > 10) {
            mobieNo = mobieNo.substring(Math.max(mobieNo.length() - 10, 0));
        }
        String regexStr = "^[0-9]{10}$";

        switch (mobieNo.charAt(0)) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
                return false;

            default:
                break;
        }

        if (mobieNo.matches(regexStr)) {
            return true;
        } else {
            return false;
        }

    }

    public static void hideKeyboardinFragment(View editText, Context activity) {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showMobileDialog(final Context context, final MobileDialogListner mobileDialogListner) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_mobile_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        final EditText edMobile, edUserName;
        Button btnOk, btnCancel;
        edMobile = (EditText) dialog.findViewById(R.id.edMobile);
        edUserName = (EditText) dialog.findViewById(R.id.edUserName);
        btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edUserName.getText().toString().isEmpty()) {
                    showToast(context, context.getString(R.string.please_enter_name));
                } else if (validateIsMobile(edMobile.getText().toString().trim())) {
                    dialog.dismiss();
                    mobileDialogListner.onMobileGet(edMobile.getText().toString().trim(), edUserName.getText().toString().trim());
                    hideKeyboardinFragment(edMobile, context);
                } else {
                    showToast(context, context.getString(R.string.please_enter_mob));
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                hideKeyboardinFragment(edMobile, context);
                mobileDialogListner.onMobileCancel();
            }
        });
        dialog.show();
    }

    public static void showOKDialog(final Context context, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_ok_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView tvMessage;
        Button btnOk;
        tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
