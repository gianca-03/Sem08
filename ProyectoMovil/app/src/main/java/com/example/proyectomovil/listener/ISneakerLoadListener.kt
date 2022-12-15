package com.example.proyectomovil.listener

import com.example.proyectomovil.model.SneakerModel

interface ISneakerLoadListener {
    fun onSneakerLoadSucces(sneakerModeList: List<SneakerModel>?)
    fun onSneakerLoadFailed(message:String?)


}