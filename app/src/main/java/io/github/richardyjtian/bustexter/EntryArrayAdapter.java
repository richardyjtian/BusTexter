package io.github.richardyjtian.bustexter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

        // Set text
        TextView bus_to_text = row.findViewById(R.id.bus_to_text);
        String busName = entry.getBusName();
        if(!busName.isEmpty())
            bus_to_text.setText("Texting: " + entry.getStopNumber() + " " + entry.getBusNumber() + "\n               " + entry.getBusName()
                + "\nBetween: " + entry.getBeginPeriod() + ":00 and " + entry.getEndPeriod() + ":00");
        else
            bus_to_text.setText("Texting: " + entry.getStopNumber() + " " + entry.getBusNumber()
                + "\nBetween: " + entry.getBeginPeriod() + ":00 and " + entry.getEndPeriod() + ":00");

        // Set text now button on click
        Button text_now = row.findViewById(R.id.text_now);
        text_now.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SMS.sendSMS("33333", entry.getStopNumber() + " " + entry.getBusNumber());
            }
        });

        // Set delete img on click
        ImageView delete = row.findViewById(R.id.bin);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.dbHandler.deleteEntry(entry.getStopNumber());
                theEntryArray.remove(pos);
                notifyDataSetChanged();
            }
        });

        return row;
    }

}
