package com.cocoba.tulisajadulu

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_toolbar.*
import kotlinx.android.synthetic.main.sheet_new_post.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity(), PostAdapter.Callback, ChildEventListener {

    private val postAdapter by lazy { PostAdapter(this) }
    private val sheetBehavior by lazy { BottomSheetBehavior.from(sheet_post) }

    /*
    * Execute notifyDataSetChanged() after the process of reading the data is completed.
    * */
    private val handler = Handler()
    private val task = Runnable {
        postAdapter.notifyDataSetChanged()
        pb_post?.gone()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()

        with(rv_post) {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }

        btn_new_post?.setOnClickListener {
            toolbar_new_post?.setTitle(R.string.title_new_post)
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            new_post_title.setText("")
            new_post_content.setText("")
            btn_delete_post?.gone()
            btn_save_post?.setOnClickListener(createPostButtonListener)
        }

        setupBottomSheet()
        postsDatabase?.addChildEventListener(this)
    }

    private fun setupToolbar() {
        toolbar_default?.title = getString(R.string.app_name)
        toolbar_default?.subtitle = currentUser?.email
        toolbar_default?.inflateMenu(R.menu.option_main)
        toolbar_default?.setOnMenuItemClickListener { logout() }
    }

    private fun setupBottomSheet() {
        sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        sheetBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(v: View, offset: Float) = Unit
            override fun onStateChanged(view: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING)
                    sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideSoftKeyboard(new_post_title)
                    hideSoftKeyboard(new_post_content)
                }
            }

        })

        toolbar_new_post?.setNavigationOnClickListener {
            sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private val createPostButtonListener = View.OnClickListener {
        val id = System.currentTimeMillis().toString()
        val title = new_post_title?.text?.toString()
        val content = new_post_content?.text?.toString()
        with(PostModel(id, title, content)) { createPost(this) }
    }

    private fun resetState() {
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        new_post_title.setText("")
        new_post_content.setText("")
    }

    private fun createPost(post: PostModel) {
        toast("Sedang menyimpan tulisan")
        postsDatabase
            ?.child(post.id ?: "")
            ?.setValue(post)
            ?.addOnSuccessListener { toast("Catatan berhasil disimpan") }
            ?.addOnFailureListener { toast("Gagal menyimpan : ${it.message}") }

        resetState()
    }

    private fun updatePost(post: PostModel) {
        toast("Sedang memperbarui tulisan")
        postsDatabase
            ?.updateChildren(post.toMap())
            ?.addOnSuccessListener { toast("Catatan berhasil disimpan") }
            ?.addOnFailureListener { toast("Gagal menyimpan : ${it.message}") }
        resetState()
    }

    private fun deleteFromDb(post: PostModel) {
        toast("Sedang menghapus tulisan")
        postsDatabase
            ?.child(post.id ?: "")
            ?.removeValue()
            ?.addOnSuccessListener { toast("Catatan berhasil dihapus") }
            ?.addOnFailureListener { toast("Gagal menghapus catatan : ${it.message}") }
        resetState()
    }

    private fun logout(): Boolean {
        AuthUI.getInstance().signOut(this)
            .addOnSuccessListener {
                startActivity<LoginActivity>()
                finish()
            }
            .addOnFailureListener {
                toast("Maaf terjadi kesalahan : ${it.message}")
            }
        return true
    }

    private fun addToAdapter(post: PostModel) {
        pb_post?.visible()
        handler.removeCallbacks(task)
        postAdapter.addItem(post)
        handler.postDelayed(task, 100L)
    }

    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
        try {
            dataSnapshot.getValue(PostModel::class.java)?.let { addToAdapter(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
        try {
            dataSnapshot.getValue(PostModel::class.java)?.let { postAdapter.update(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        try {
            dataSnapshot.getValue(PostModel::class.java)?.let { postAdapter.delete(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) = Unit

    override fun onCancelled(databaseError: DatabaseError) = Unit

    override fun onPostClick(item: PostModel) {
        toolbar_new_post?.setTitle(R.string.title_edit_post)
        new_post_content?.setText(item.content)
        new_post_title?.setText(item.title)

        btn_save_post?.setOnClickListener {
            item.title = new_post_title?.text?.toString()
            item.content = new_post_content?.text?.toString()
            updatePost(item)
        }

        btn_delete_post?.visible()
        btn_delete_post?.setOnClickListener { deleteFromDb(item) }
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onBackPressed() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        else finish()
    }
}
