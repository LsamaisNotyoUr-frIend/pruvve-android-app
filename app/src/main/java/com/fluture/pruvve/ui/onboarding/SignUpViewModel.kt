package com.fluture.pruvve.ui.onboarding

import androidx.lifecycle.ViewModel
import com.fluture.pruvve.data.repository.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: SignUpRepository,
): ViewModel() {
}