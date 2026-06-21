package com.mesawa.cuidarproximocuidador.ui.perfil.configuracoes

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class ConfiguracoesActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, ConfiguracoesFragment())
                .commit()
        }
    }
}
