package com.cocoba.tulisajadulu

import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Chandra on 07/03/19.
 * Need some help?
 * Contact me at y.pristyan.chandra@gmail.com
 */

object DBConstant {

    /* Under UID */
    const val TABLE_NAME = "post"

}

val firebaseDatabase
    get() = FirebaseDatabase.getInstance()

val database
    get() = currentUser?.uid?.let { firebaseDatabase.reference.child(it) }

val postsDatabase
    get() = database?.child(DBConstant.TABLE_NAME)

