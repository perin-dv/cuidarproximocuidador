package com.mesawa.cuidarproximocuidador.ui.perfil.avaliacoes

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class AvaliacoesActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, AvaliacoesFragment())
                .commit()
        }
    }
}
