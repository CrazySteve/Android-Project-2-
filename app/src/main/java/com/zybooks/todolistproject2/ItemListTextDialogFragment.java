package com.zybooks.todolistproject2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ItemListTextDialogFragment extends DialogFragment {

    public boolean confirmedSelection;
    public EditText newItemEditText;
    public String newItemText;

    public ItemDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.item_list_dialog, null);
        builder.setView(view);
        builder.setTitle(R.string.ItemListDialogTitle)

                .setPositiveButton(R.string.okString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newItemText = newItemEditText.getText().toString();
                        listener.listenText(newItemText);
                        confirmedSelection = true;
                    }
                })
                .setNegativeButton(R.string.cancelString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        confirmedSelection = false;
                    }
                });
        // Create the AlertDialog object and return it

        newItemEditText = view.findViewById(R.id.new_item_text);

        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            listener = (ItemDialogListener) context;
        }catch(ClassCastException e)
        {
            throw new ClassCastException(context.toString() +
                    "must implement ItemDialogListener");
        }
    }

    public interface ItemDialogListener{
        void listenText(String newItemText);
    }
}
