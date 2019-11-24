package rrapps.myplaces.view.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import rrapps.myplaces.R;

/**
 *
 * Created by abhishek on 14/09/16.
 */

public class EditableTextView extends LinearLayout implements View.OnClickListener {

    private ImageButton mImageButton;

    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.mOnTextChangedListener = onTextChangedListener;
    }

    public interface OnTextChangedListener {
        void onTextChanged(String text);
    }

    private OnTextChangedListener mOnTextChangedListener;
    private TextView mTextView;
    private EditText mEditText;

    private enum State {
        NON_EDITABLE,
        EDITABLE
    }

    private State mCurrentState = State.NON_EDITABLE;

    public EditableTextView(Context context, OnTextChangedListener onTextChangedListener) {
        super(context);
        mOnTextChangedListener = onTextChangedListener;
        initializeViews(context);
    }

    public EditableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
        setupAttributes(context, attrs);
    }

    private void setupAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context
                .obtainStyledAttributes(attrs, R.styleable.editableTextView);
        String text = typedArray.getString(R.styleable.editableTextView_text);
        mTextView.setText(text);

        typedArray.recycle();
    }

    public EditableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
        setupAttributes(context, attrs);
    }

    @RequiresApi(21)
    public EditableTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeViews(context);
        setupAttributes(context, attrs);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.view_editable_textview, this);
        mImageButton = (ImageButton) rootView.findViewById(R.id.btn_toggle);
        mImageButton.setOnClickListener(this);

        mTextView = (TextView)rootView.findViewById(R.id.tv_text);
        mEditText = (EditText) rootView.findViewById(R.id.et_text);
    }

    @Override
    public void onClick(View view) {
        if(mCurrentState == State.EDITABLE) {
            mTextView.setVisibility(VISIBLE);
            mTextView.setText(mEditText.getText());
            mEditText.setVisibility(GONE);
            mImageButton.setImageResource(R.drawable.ic_edit_content);

            mCurrentState = State.NON_EDITABLE;

            if(mOnTextChangedListener != null) {
                mOnTextChangedListener.onTextChanged(mTextView.getText().toString());
            }
        } else {
            mTextView.setVisibility(GONE);
            mEditText.setText(mTextView.getText());
            mEditText.setVisibility(VISIBLE);
            mCurrentState = State.EDITABLE;
            mImageButton.setImageResource(R.drawable.ic_action_done);
        }
    }

    public void setText(String text) {
        if(mCurrentState == State.EDITABLE) {
            mEditText.setText(text);
        } else {
            mTextView.setText(text);
        }
    }

    public String getText() {
        if(mCurrentState == State.EDITABLE) {
            return mEditText.getText().toString();
        } else {
            return mTextView.getText().toString();
        }
    }
}
