package com.hermitfang.socialdictionary.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.hermitfang.socialdictionary.R;
import com.hermitfang.socialdictionary.activities.WordListActivity;

public class ConfirmRemoveDialog extends DialogFragment {
    private EditText mEditText;
    private static WordListActivity wordListActivity;

    public ConfirmRemoveDialog() {
        // Empty constructor required for DialogFragment
    }

    public static ConfirmRemoveDialog newInstance(String word, WordListActivity activity) {
        ConfirmRemoveDialog frag = new ConfirmRemoveDialog();
        Bundle args = new Bundle();
        args.putString("word", word);
        frag.setArguments(args);
        wordListActivity = activity;
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getString(R.string.dialog_remove_from_wordlist);
        String yes = getString(R.string.dialog_yes);
        String no = getString(R.string.dialog_no);
        final String word = getArguments().getString("word");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Do you want to remove '" + word + "' from your wordlist?");
        // alertDialogBuilder.setMessage(getString(R.string.dialog_remove_this_from_wordlist) + ": " + word);
        alertDialogBuilder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                wordListActivity.removeWord(word);
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
