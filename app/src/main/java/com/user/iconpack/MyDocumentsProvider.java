package com.user.iconpack;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyDocumentsProvider extends DocumentsProvider {
    private static final String ROOT_ID = "root";
    private static final String DOC_ID = "doc1";
    private static final String DOC_DISPLAY_NAME = "Pick a file (placeholder).txt";
    private static final String DOC_MIME_TYPE = "text/plain";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor queryRoots(String[] projection) throws FileNotFoundException {
        final MatrixCursor result = new MatrixCursor(projection != null ? projection : DocumentsContract.Root.getProjection());

        final MatrixCursor.RowBuilder row = result.newRow();
        // Required columns
        row.add(DocumentsContract.Root.COLUMN_ROOT_ID, ROOT_ID);
        row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, DOC_ID);
        row.add(DocumentsContract.Root.COLUMN_TITLE, "Icon Pack Files");
        // Flags: supports create, local only
        row.add(DocumentsContract.Root.COLUMN_FLAGS, DocumentsContract.Root.FLAG_SUPPORTS_CREATE);
        row.add(DocumentsContract.Root.COLUMN_AVAILABLE_BYTES, 0);

        return result;
    }

    @Override
    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder) throws FileNotFoundException {
        final MatrixCursor result = new MatrixCursor(projection != null ? projection : DocumentsContract.Document.getProjection());
        if (ROOT_ID.equals(parentDocumentId)) {
            MatrixCursor.RowBuilder row = result.newRow();
            row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DOC_ID);
            row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, DOC_DISPLAY_NAME);
            row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, DOC_MIME_TYPE);
            row.add(DocumentsContract.Document.COLUMN_FLAGS, DocumentsContract.Document.FLAG_SUPPORTS_WRITE);
            row.add(DocumentsContract.Document.COLUMN_SIZE, 16);
        }
        return result;
    }

    @Override
    public Cursor queryDocument(String documentId, String[] projection) throws FileNotFoundException {
        final MatrixCursor result = new MatrixCursor(projection != null ? projection : DocumentsContract.Document.getProjection());
        if (DOC_ID.equals(documentId)) {
            MatrixCursor.RowBuilder row = result.newRow();
            row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DOC_ID);
            row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, DOC_DISPLAY_NAME);
            row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, DOC_MIME_TYPE);
            row.add(DocumentsContract.Document.COLUMN_FLAGS, DocumentsContract.Document.FLAG_SUPPORTS_WRITE);
            row.add(DocumentsContract.Document.COLUMN_SIZE, 16);
        }
        return result;
    }

    @Override
    public ParcelFileDescriptor openDocument(String documentId, String mode, CancellationSignal signal) throws FileNotFoundException {
        if (!DOC_ID.equals(documentId)) throw new FileNotFoundException("Document not found: " + documentId);

        try {
            Context ctx = getContext();
            File f = new File(ctx.getCacheDir(), "placeholder.txt");
            if (!f.exists()) {
                FileOutputStream fos = new FileOutputStream(f);
                fos.write("This is a placeholder file.".getBytes());
                fos.flush();
                fos.close();
            }
            return ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (IOException e) {
            throw new FileNotFoundException("Failed to open file: " + e.getMessage());
        }
    }

    @Override
    public String getDocumentType(String documentId) throws FileNotFoundException {
        if (DOC_ID.equals(documentId)) return DOC_MIME_TYPE;
        throw new FileNotFoundException("Document not found: " + documentId);
    }
}
