package com.mesawa.cuidarproximocuidador.ui.perfil.suporte

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class SuporteSacActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, SuporteSacFragment())
                .commit()
        }
    }
}
