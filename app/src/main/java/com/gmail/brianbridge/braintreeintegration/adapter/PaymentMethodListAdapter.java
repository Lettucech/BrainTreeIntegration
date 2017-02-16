package com.gmail.brianbridge.braintreeintegration.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.braintreepayments.api.models.PaymentMethodNonce;
import com.gmail.brianbridge.braintreeintegration.R;
import com.gmail.brianbridge.braintreeintegration.utils.PaymentMethodType;

import java.util.List;

public class PaymentMethodListAdapter extends ArrayAdapter<PaymentMethodNonce> {
	public static final String TAG = PaymentMethodListAdapter.class.getSimpleName();

	static class ViewHolder {
		public ImageView imageView;
		public TextView textView;
	}

	public PaymentMethodListAdapter(Context context, int resource, List<PaymentMethodNonce> objects) {
		super(context, resource, objects);
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.view_payment_method, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
			viewHolder.textView = (TextView) view.findViewById(R.id.textView);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		PaymentMethodNonce nonce = getItem(position);
		PaymentMethodType type = PaymentMethodType.forType(nonce);
		String description = nonce.getDescription();
		if (nonce.isDefault()) {
			description += " (Default)";
		}

		viewHolder.imageView.setImageResource(type.getVaultedDrawable());
		viewHolder.textView.setText(description);
		return view;
	}
}
