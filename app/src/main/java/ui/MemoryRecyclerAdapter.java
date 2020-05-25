package ui;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorylane.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Memory;

public class MemoryRecyclerAdapter extends RecyclerView.Adapter<MemoryRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Memory> memoryList;

    public MemoryRecyclerAdapter(Context context, List<Memory> memoryList) {
        this.context = context;
        this.memoryList = memoryList;
    }

    @NonNull
    @Override
    public MemoryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.memory_row,parent,false);

        return new ViewHolder(v,context);
    }

    @Override
    public void onBindViewHolder(@NonNull final MemoryRecyclerAdapter.ViewHolder holder, final int position) {

        final Memory memory = memoryList.get(position);
        holder.title.setText(memory.getTitle());

        holder.desc.setText(memory.getDesc());

        String imageurl = memory.getImageUrl();

        Picasso.get().load(imageurl).placeholder(R.drawable.image).fit().into(holder.imageView);

        String timePosted = (String) DateUtils.getRelativeTimeSpanString(context,memory.getTimeAdded()
                .getSeconds()*1000);

        holder.date.setText(timePosted);


    }



    @Override
    public int getItemCount() {
        return memoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title, desc, date;
        public ImageView imageView;
        public ImageButton shareBtn;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context=ctx;
            title = itemView.findViewById(R.id.memory_list_title);
            desc = itemView.findViewById(R.id.memory_list_desc);
            date = itemView.findViewById(R.id.memory_list_date);
            imageView = itemView.findViewById(R.id.memory_list_image);

            shareBtn = itemView.findViewById(R.id.memory_list_share);

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "Hey check out my memory");

                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                }
            });
        }
    }
}
