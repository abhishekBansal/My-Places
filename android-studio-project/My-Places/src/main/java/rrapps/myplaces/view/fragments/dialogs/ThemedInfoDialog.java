package rrapps.myplaces.view.fragments.dialogs;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;
import lombok.Setter;
import rrapps.myplaces.R;

public class ThemedInfoDialog extends DialogFragment {

    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TITLE = "title";
    public static final String KEY_POSITIVE_BUTTON_TEXT = "positive_button_text";
    private static final String KEY_NEGATIVE_BUTTON_TEXT = "negative_button_text";
    private static final String KEY_SHOW_CANCEL_BUTTON = "show_cancel_button";

    @Getter
    @Setter
    private View.OnClickListener mPositiveClickListener;

    @Getter
    @Setter
    private View.OnClickListener mNegativeClickListener;

    @BindView(R.id.tv_message)
    TextView mMessage;

    @BindView(R.id.btn_save)
    Button mOkButton;

    @BindView(R.id.btn_no)
    Button mCancelButton;
    boolean mButton;


    public static ThemedInfoDialog newInstance(String title, String message, String positiveText,
                                               String negativeText, boolean cancelButton) {
        Bundle bundle = createBundle(title, message, positiveText, negativeText, cancelButton);
        ThemedInfoDialog dialog = new ThemedInfoDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    protected static Bundle createBundle(String title, String message,
                                         String positiveButtonText, String negativeButtonText, boolean cancelButton) {
        Bundle args = new Bundle();
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_TITLE, title);
        args.putString(KEY_POSITIVE_BUTTON_TEXT, positiveButtonText);
        args.putString(KEY_NEGATIVE_BUTTON_TEXT, negativeButtonText);
        args.putBoolean(KEY_SHOW_CANCEL_BUTTON, cancelButton);

        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_themed_dialog, container, false);
        ButterKnife.bind(this, rootView);
        if(getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        Bundle args = getArguments();
        mMessage.setText(args.getString(KEY_MESSAGE));
        mCancelButton.setVisibility(View.GONE);
        mButton = args.getBoolean(KEY_SHOW_CANCEL_BUTTON);
        if (mButton) {
            mCancelButton.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(args.getString(KEY_POSITIVE_BUTTON_TEXT))) {
            mOkButton.setText(getString(android.R.string.ok));
        } else {
            mOkButton.setText(args.getString(KEY_POSITIVE_BUTTON_TEXT));
        }

        if (!TextUtils.isEmpty(args.getString(KEY_NEGATIVE_BUTTON_TEXT))) {
            mCancelButton.setText(args.getString(KEY_NEGATIVE_BUTTON_TEXT));
        }

        return rootView;
    }

    @OnClick(R.id.btn_save)
    void onClickPositive(View view) {
        dismiss();
        if (mPositiveClickListener != null) {
            mPositiveClickListener.onClick(view);
        }
    }


    @OnClick(R.id.btn_no)
    public void onClickNegative(View view) {
        dismiss();
        if(mNegativeClickListener != null) {
            mNegativeClickListener.onClick(view);
        }
    }
}
