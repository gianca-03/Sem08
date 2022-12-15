package com.example.proyectomovil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide.init
import com.example.proyectomovil.adapter.MySneakerAdapter
import com.example.proyectomovil.eventbus.UpdateCartEvent
import com.example.proyectomovil.listener.ICartLoadListener
import com.example.proyectomovil.listener.ISneakerLoadListener
import com.example.proyectomovil.model.CartModel
import com.example.proyectomovil.model.SneakerModel
import com.example.proyectomovil.utils.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), ISneakerLoadListener,ICartLoadListener {

    lateinit var sneakerLoadListener: ISneakerLoadListener
    lateinit var cartLoadListener: ICartLoadListener

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        loadSneakerFromFirebase()
        countCartFromFirebase()

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public fun onUpdateCartEvent(event:UpdateCartEvent)
    {
        countCartFromFirebase()
    }

    private fun countCartFromFirebase() {
        val  cartModels: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object:ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    for(cartSnapshot in snapshot.children){
                        val cartModel =  cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }

            })
    }

    private fun loadSneakerFromFirebase() {
        val sneakerModels: MutableList<SneakerModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Sneakers")
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (sneakerSnapshot in snapshot.children){
                            val  sneakerModel = sneakerSnapshot.getValue(SneakerModel::class.java)
                            sneakerModel!!.Key = sneakerSnapshot.key
                            sneakerModels.add(sneakerModel)
                        }
                        sneakerLoadListener.onSneakerLoadSucces(sneakerModels)
                    } else {
                        sneakerLoadListener.onSneakerLoadFailed("Sneaker items don't exists")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                   sneakerLoadListener.onSneakerLoadFailed(error.message)
                }

            })
    }

    private fun init(){
        sneakerLoadListener = this
        cartLoadListener = this

        val gridLayoutManager =  GridLayoutManager(this,2)
        recycler_sneaker.layoutManager = gridLayoutManager
        recycler_sneaker.addItemDecoration( SpaceItemDecoration() )
    }

    override fun onSneakerLoadSucces(sneakerModeList: List<SneakerModel>?) {
        val adapter =  MySneakerAdapter(this,sneakerModeList!!,cartLoadListener)
        recycler_sneaker.adapter = adapter
    }

    override fun onSneakerLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_LONG).show()
    }

    override fun onLoadCartSuccess(cartModeList: List<CartModel>) {
        var cartSum = 0
        for(cartModel in cartModeList!!) cartSum+= cartModel!!.quantity
        badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_LONG).show()
    }
}