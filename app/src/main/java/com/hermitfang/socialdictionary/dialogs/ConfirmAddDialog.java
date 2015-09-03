package com.hermitfang.socialdictionary.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.hermitfang.socialdictionary.R;
import com.hermitfang.socialdictionary.models.WordlistManager;

public class ConfirmAddDialog extends DialogFragment {

    private EditText mEditText;

    public ConfirmAddDialog() {
        // Empty constructor required for DialogFragment
    }

    public static ConfirmAddDialog newInstance(String word) {
        ConfirmAddDialog frag = new ConfirmAddDialog();
        Bundle args = new Bundle();
        args.putString("word", word);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getString(R.string.dialog_add_to_wordlist);
        String yes = getString(R.string.dialog_yes);
        String no = getString(R.string.dialog_no);
        final String word = getArguments().getString("word");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Do you want to add '" + word + "' to your own wordlist?");
        // alertDialogBuilder.setMessage(getString(R.string.dialog_add_this_into_wordlist) + ": " + word);
        alertDialogBuilder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                WordlistManager m = new WordlistManager(getActivity());
                m.addWord(word);
            }
        });
        alertDialogBuilder.setNegativeButton(no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }
}