package io.github.richardyjtian.bustexter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class EntryArrayAdapter extends ArrayAdapter<Entry> {
    // my new class variables, copies of constructor params, but add more if required
    private Context context;
    private ArrayList<Entry> theEntryArray;

    // Constructor
    public EntryArrayAdapter(Context _context, int textViewResourceId, ArrayList<Entry> _theEntryArray)
    {
        // call base class constructor
        super(_context, textViewResourceId, _theEntryArray);

        // save the context and the array of strings we were given
        context = _context;
        theEntryArray = _theEntryArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View row = inflater.inflate (R.layout.entry_row, parent, false );
        final int pos = position;
        final Entry entry = theEntryArray.get(position);

        final int stopNumber = entry.getStopNumber();
        final int busNumber = entry.getBusNumber();
        final int beginPeriod = entry.getBeginPeriod();
        final int endPeriod = entry.getEndPeriod();

        // Set text
        TextView bus_to_text = row.findViewById(R.id.bus_to_text);
        String busName = entry.getBusName();
        String address = entry.getAddress();
        String text;

        if(!busName.isEmpty() && !address.isEmpty()) {
            text = "Texting: " + stopNumber + " " + busNumber +
                    "\nTo: " + busName +
                    "\nBetween: " + beginPeriod + ":00 and " + endPeriod + ":00" +
                    "\nNear: " + address;
        } else if(!busName.isEmpty()) {
            text = "Texting: " + stopNumber + " " + busNumber +
                    "\nTo: " + busName +
                    "\nBetween: " + beginPeriod + ":00 and " + endPeriod + ":00";
        } else if(!address.isEmpty()) {
            text = "Texting: " + stopNumber + " " + busNumber +
                    "\nBetween: " + beginPeriod + ":00 and " + endPeriod + ":00" +
                    "\nNear: " + address;
        } else {
            text = "Texting: " + stopNumber + " " + busNumber +
                    "\nBetween: " + beginPeriod + ":00 and " + endPeriod + ":00";
        }
        bus_to_text.setText(text);


        // Set text now button on click
        Button text_now = row.findViewById(R.id.text_now);
        text_now.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String busStop = stopNumber + " " + busNumber;
                MySMS.sendSMS("33333", busStop);
                Toast.makeText(context, "Bus stop texted: " + busStop, Toast.LENGTH_LONG).show();
            }
        });

        // Set delete img on click
        ImageView delete = row.findViewById(R.id.bin);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.dbHandler.deleteEntry(stopNumber);
                theEntryArray.remove(pos);
                notifyDataSetChanged();
            }
        });

        return row;
    }

}
