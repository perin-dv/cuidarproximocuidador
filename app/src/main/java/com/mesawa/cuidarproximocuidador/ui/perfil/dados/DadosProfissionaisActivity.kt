package com.mesawa.cuidarproximocuidador.ui.perfil.dados

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class DadosProfissionaisActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, DadosProfissionaisFragment())
                .commit()
        }
    }
}
