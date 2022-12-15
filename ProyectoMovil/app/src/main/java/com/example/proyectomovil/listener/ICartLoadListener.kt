package com.example.proyectomovil.listener

import com.example.proyectomovil.model.CartModel

interface ICartLoadListener {
    fun onLoadCartSuccess(cartModeList:List<CartModel>)
    fun onLoadCartFailed(message:String?)
}