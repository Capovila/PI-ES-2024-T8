package br.com.projetopi.smartlock

import androidx.fragment.app.Fragment

/***
 * Interface usada para comunicar a main actrivity
 * para trocar o fragmento exibido
 */
interface FragmentHandler {
    fun changeFragment(fragment: Fragment)
}