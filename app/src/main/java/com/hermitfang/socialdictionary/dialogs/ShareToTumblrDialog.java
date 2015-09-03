package com.hermitfang.socialdictionary.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.hermitfang.socialdictionary.R;

public class ShareToTumblrDialog extends DialogFragment {
    // private EditText mEditText;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    public ShareToTumblrDialog() {
    }

    // public ShareToTumblrDialog() {
        // Empty constructor required for DialogFragment
    // }

    public static ShareToTumblrDialog newInstance(String flag) {
        ShareToTumblrDialog frag = new ShareToTumblrDialog();
        Bundle args = new Bundle();
        args.putString("flag", flag);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getString(R.string.action_share_game_success);
        String yes = getString(R.string.dialog_yes);
        String no = getString(R.string.dialog_no);
        final String flag = getArguments().getString("flag");
        String shareSentance;

        if ( flag.equalsIgnoreCase("share")) {
            title = "Share to Tumblr";
            shareSentance = "Do you want to share to Tumblr ?";
        } else {
            title = getString(R.string.action_share_game_success);
            shareSentance = "Do you want to share your success to Tumblr";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(shareSentance)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(ShareToTumblrDialog.this);
                    }
                })
                .setNegativeButton(no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(ShareToTumblrDialog.this);
                    }
                });

        return alertDialogBuilder.create();
    }

    // public abstract void callOKFunc();
}
