package com.mesawa.cuidarproximocuidador

import android.content.Intent
import android.os.Bundle
import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.mesawa.cuidarproximocuidador.Login.LoginActivity
import com.mesawa.cuidarproximocuidador.ui.HomeCuidadorActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val destino = if (FirebaseAuth.getInstance().currentUser == null) {
            LoginActivity::class.java
        } else {
            HomeCuidadorActivity::class.java
        }

        startActivity(Intent(this, destino))
        finish()
    }
}
