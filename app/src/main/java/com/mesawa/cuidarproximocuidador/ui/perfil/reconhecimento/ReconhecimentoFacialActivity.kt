package com.mesawa.cuidarproximocuidador.ui.perfil.reconhecimento

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class ReconhecimentoFacialActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, ReconhecimentoFacialFragment())
                .commit()
        }
    }
}
