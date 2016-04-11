package de.evolutionid.nfctesting06.ui;


import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import de.evolutionid.nfctesting06.R;

/**
 * Created by Moritz Korsch on 11.04.2016.
 */
public class ContactListFragment extends Fragment implements
        AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY :
                    Contacts.DISPLAY_NAME
    };

    //endregion

    //region Arrays
    /**
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            android.R.id.text1
    };
    //TODO: Probably only need the LOOKUP_KEY. If this is the case, delete other lines. Change LOOKUP_KEY_INDEX accordingly!!!
    private static final String[] PROJECTION = new String[]{
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY :
                    Contacts.DISPLAY_NAME
    };

    //endregion

    //region User selection variables
    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;
    // Defines the text expression
    private static final String SELECTION = Contacts.DISPLAY_NAME + " LIKE ?";
    //region GUI
    ListView mContactsList;

    //endregion
    //The contact's _ID value
    long mContactId;
    //The contact's LOOKUP_KEY
    private String mContactKey;
    //A content URI for the selected contact
    private Uri mContactUri;
    //An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;
    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = {mSearchString};


    /**
     * Fragments require an empty constructor.
     */
    public ContactListFragment() {
    }

    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.contact_list_fragment,
                container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Gets the ListView from the View list of the parent activity
        mContactsList =
                (ListView) getActivity().findViewById(R.layout.contact_list_view);
        // Gets a CursorAdapter
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.contact_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);

        // Set the item click listener to be the current fragment.
        mContactsList.setOnItemClickListener(this);

        class ContactsFragment extends Fragment implements
                LoaderManager.LoaderCallbacks<Cursor> {
            // Called just before the Fragment displays its UI
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                // Always call the super method first
                super.onActivityCreated(savedInstanceState);
                // Initializes the loader
                getLoaderManager().initLoader(0, null, this);
            }

            @Override
            public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
            /*
             * Makes search string into pattern and
             * stores it in the selection array
             */
                mSelectionArgs[0] = "%" + mSearchString + "%";
                // Starts the query
                return new CursorLoader(
                        getActivity(),
                        Contacts.CONTENT_URI,
                        PROJECTION,
                        SELECTION,
                        mSelectionArgs,
                        null
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                // Put the result Cursor in the adapter for the ListView
                mCursorAdapter.swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                // Delete the reference to the existing Cursor
                mCursorAdapter.swapCursor(null);
            }
        }
    }


    //region Auto-generated stuff
    //TODO: CHECK EVERYTHING

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //endregion

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long rowID) {
        AdapterView<?> parent, View view,int position, long rowID){
            // Get the Cursor
            Cursor cursor = parent.getAdapter().getCursor();
            // Move to the selected contact
            cursor.moveToPosition(position);
            // Get the _ID value
            mContactId = getLong(CONTACT_ID_INDEX);
            // Get the selected LOOKUP KEY
            mContactKey = getString(CONTACT_KEY_INDEX);
            // Create the contact's content Uri
            mContactUri = Contacts.getLookupUri(mContactId, mContactKey);
        /*
         * You can use mContactUri as the content URI for retrieving
         * the details for a contact.
         */
        }
    }


}
