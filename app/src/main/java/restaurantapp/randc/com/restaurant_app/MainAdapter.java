package restaurantapp.randc.com.restaurant_app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


    public class MainAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<MainItem> list;
        private View mView;
        private boolean isLoadingAdded = false;
        private Context mContext;

        // View Holder class which
        // extends RecyclerView.ViewHolder
        public class donationView
                extends RecyclerView.ViewHolder {

            // Text View
            TextView nameText;
            TextView locationtText;
            TextView typeText;
            TextView resaurantTypeText;
            TextView weightText;
            ImageView mainImage;
            TextView followerText;
            TextView timeText;
            TextView fruitview;
            TextView veggiesview;
            TextView dairyview;
            TextView meatview;
            TextView dishesview;
            TextView grainsview;

            // parameterised constructor for View Holder class
            // which takes the view as a parameter
            public donationView(View view)
            {
                super(view);

                nameText = view.findViewById(R.id.mainListName);
                locationtText = view.findViewById(R.id.mainListAddr);
                typeText = view.findViewById(R.id.mainType);
                resaurantTypeText = view.findViewById(R.id.typeMain);
                timeText = view.findViewById(R.id.timeMain);
                weightText = view.findViewById(R.id.MainWeight);
                mainImage = view.findViewById(R.id.mainImage);
                followerText = view.findViewById(R.id.mainFollowers);
                fruitview = view.findViewById(R.id.fruitsPop);
                veggiesview = view.findViewById(R.id.vegetablesPop);
                dairyview = view.findViewById(R.id.dairyPop);
                dishesview = view.findViewById(R.id.dishesPop);
                grainsview = view.findViewById(R.id.grainsPop);
                meatview = view.findViewById(R.id.meatPop);









            }
        }



        // Constructor for adapter class
        // which takes a list of String type
        public MainAdapter(Context context, ArrayList<MainItem> mainItems)
        {
            list = mainItems;
            mContext = context;
        }

        public List<MainItem> getList() {
            return list;
        }

        public void setList(List<MainItem> list) {
            this.list = list;
        }

        // Override onCreateViewHolder which deals
        // with the inflation of the card layout
        // as an item for the RecyclerView.
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {

            // Inflate item.xml using LayoutInflator
            RecyclerView.ViewHolder ViewHolder = null;
            LayoutInflater inflater  = LayoutInflater.from(parent.getContext());

            ViewHolder = new donationView(inflater.inflate(R.layout.main_item,
                        parent,
                        false));




            // return itemView
            return ViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder CommonHolder,
                                      int position) {


                    donationView holder = (donationView) CommonHolder;
                    holder.nameText.setText(list.get(position).getName());
                    holder.typeText.setText(list.get(position).getType());
                    holder.resaurantTypeText.setText(list.get(position).getTypeRestaurant());
                    holder.weightText.setText(list.get(position).getWeight());
                    holder.followerText.setText(list.get(position).getFollowers());
                    holder.timeText.setText(list.get(position).getTime());
                    holder.locationtText.setText(list.get(position).getLocation());
                    //holder.mainImage.setImageResource(list.get(position).getImage());

                    int width = Resources.getSystem().getDisplayMetrics().widthPixels;

                    if (list.get(position).isFruits()) {
                        holder.fruitview.setVisibility(View.VISIBLE);
                    } else {
                        holder.fruitview.setVisibility(View.GONE);
                    }

                    if (list.get(position).isVegetables()) {
                        holder.veggiesview.setVisibility(View.VISIBLE);
                    } else {
                        holder.veggiesview.setVisibility(View.GONE);
                    }

                    if (list.get(position).isDairy()) {
                        holder.dairyview.setVisibility(View.VISIBLE);
                    } else {
                        holder.dairyview.setVisibility(View.GONE);
                    }

                    if (list.get(position).isDishes()) {
                        holder.dishesview.setVisibility(View.VISIBLE);
                    } else {
                        holder.dishesview.setVisibility(View.GONE);
                    }

                    if (list.get(position).isGrains()) {
                        holder.grainsview.setVisibility(View.VISIBLE);
                    } else {
                        holder.grainsview.setVisibility(View.GONE);
                    }

                    if (list.get(position).isMeat()) {
                        holder.meatview.setVisibility(View.VISIBLE);
                    } else {
                        holder.meatview.setVisibility(View.GONE);
                    }
                    int height = (int) ((width * 2) / 3);

                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .borderWidthDp(0)
                            .cornerRadiusDp(30)
                            .oval(false)
                            .build();

                    Picasso.get()
                            .load(list.get(position).getImage())
                            .resize(width, height)
                            .transform(transformation)
                            .centerCrop()
                            .into(holder.mainImage);
                }






        // Override getItemCount which Returns
        // the length of the RecyclerView.
        @Override

        public int getItemCount() {
            return list == null ? 0 : list.size();
        }


        public void add(MainItem addition) {
            list.add(addition);
            Log.d("TAG", "addAll: new item added");
            notifyItemInserted(list.size() - 1);
        }

        public void addAll(List<MainItem> ItemList) {
            for (int i = 0;i<ItemList.size();i++) {
                add(ItemList.get(i));

            }

        }

        public void remove(MainItem item) {
            int position = list.indexOf(item);
            if (position > -1) {
                list.remove(position);
                notifyItemRemoved(position);
            }
        }

        public void clear() {
            isLoadingAdded = false;
            while (getItemCount() > 0) {
                remove(getItem(0));
            }
        }

        public boolean isEmpty() {
            return getItemCount() == 0;
        }




        public MainItem getItem(int position) {
            return list.get(position);
        }

    }


