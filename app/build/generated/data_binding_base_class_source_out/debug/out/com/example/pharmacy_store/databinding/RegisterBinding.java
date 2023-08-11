// Generated by view binder compiler. Do not edit!
package com.example.pharmacy_store.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.pharmacy_store.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RegisterBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button button;

  @NonNull
  public final EditText cpasswordEditText;

  @NonNull
  public final EditText emailEditText2;

  @NonNull
  public final EditText nameEditText;

  @NonNull
  public final EditText passwordEditText;

  @NonNull
  public final TextView textView;

  @NonNull
  public final TextView textView2;

  @NonNull
  public final TextView textView3;

  @NonNull
  public final TextView textView4;

  private RegisterBinding(@NonNull ConstraintLayout rootView, @NonNull Button button,
      @NonNull EditText cpasswordEditText, @NonNull EditText emailEditText2,
      @NonNull EditText nameEditText, @NonNull EditText passwordEditText,
      @NonNull TextView textView, @NonNull TextView textView2, @NonNull TextView textView3,
      @NonNull TextView textView4) {
    this.rootView = rootView;
    this.button = button;
    this.cpasswordEditText = cpasswordEditText;
    this.emailEditText2 = emailEditText2;
    this.nameEditText = nameEditText;
    this.passwordEditText = passwordEditText;
    this.textView = textView;
    this.textView2 = textView2;
    this.textView3 = textView3;
    this.textView4 = textView4;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RegisterBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RegisterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.register, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RegisterBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button;
      Button button = ViewBindings.findChildViewById(rootView, id);
      if (button == null) {
        break missingId;
      }

      id = R.id.cpasswordEditText;
      EditText cpasswordEditText = ViewBindings.findChildViewById(rootView, id);
      if (cpasswordEditText == null) {
        break missingId;
      }

      id = R.id.emailEditText2;
      EditText emailEditText2 = ViewBindings.findChildViewById(rootView, id);
      if (emailEditText2 == null) {
        break missingId;
      }

      id = R.id.nameEditText;
      EditText nameEditText = ViewBindings.findChildViewById(rootView, id);
      if (nameEditText == null) {
        break missingId;
      }

      id = R.id.passwordEditText;
      EditText passwordEditText = ViewBindings.findChildViewById(rootView, id);
      if (passwordEditText == null) {
        break missingId;
      }

      id = R.id.textView;
      TextView textView = ViewBindings.findChildViewById(rootView, id);
      if (textView == null) {
        break missingId;
      }

      id = R.id.textView2;
      TextView textView2 = ViewBindings.findChildViewById(rootView, id);
      if (textView2 == null) {
        break missingId;
      }

      id = R.id.textView3;
      TextView textView3 = ViewBindings.findChildViewById(rootView, id);
      if (textView3 == null) {
        break missingId;
      }

      id = R.id.textView4;
      TextView textView4 = ViewBindings.findChildViewById(rootView, id);
      if (textView4 == null) {
        break missingId;
      }

      return new RegisterBinding((ConstraintLayout) rootView, button, cpasswordEditText,
          emailEditText2, nameEditText, passwordEditText, textView, textView2, textView3,
          textView4);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
