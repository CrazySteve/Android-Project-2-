package com.zybooks.todolistproject2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zybooks.todolistproject2.dummy.DummyContent;


public class DeleteConfirmationDialogFragment extends DialogFragment {
    public boolean didConfirm;


   // public DeleteConfirmationDialogFragment.DeleteConfirmationDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_delete_confirmation_dialog, null);
        builder.setView(view);
        builder.setTitle(R.string.delete_dialog_title)

                .setPositiveButton(R.string.delete_confirmation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        didConfirm = true;
                        ItemListActivity.deleteConfirmation.confirm();

                    }
                })
                .setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        didConfirm = false;
                    }
                });

        return builder.create();
    }

  /*  @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            listener = (DeleteConfirmationDialogFragment.DeleteConfirmationDialogListener) context;
        }catch(ClassCastException e)
        {
            throw new ClassCastException(context.toString() +
                    "must implement ItemDialogListener");
        }

    }


    public interface DeleteConfirmationDialogListener{
        void listenText(boolean didConfirm);
    }*/
}
