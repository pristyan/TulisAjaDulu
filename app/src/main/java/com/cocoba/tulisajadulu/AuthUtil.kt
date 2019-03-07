package com.cocoba.tulisajadulu

import com.google.firebase.auth.FirebaseAuth

/**
 * Created by Chandra on 07/03/19.
 * Need some help?
 * Contact me at y.pristyan.chandra@gmail.com
 */
val auth
    get() = FirebaseAuth.getInstance()

val currentUser
    get() = FirebaseAuth.getInstance().currentUser