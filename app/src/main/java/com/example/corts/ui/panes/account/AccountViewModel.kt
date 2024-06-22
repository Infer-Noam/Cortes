package com.example.corts.ui.panes.account

import androidx.lifecycle.ViewModel
import com.example.corts.data.model.AccountUiState
import com.example.corts.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class LEAGUE{
    BRONZE,
    SILVER,
    GOLD
}

@HiltViewModel
class AccountViewModel @Inject constructor(
   private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountUiState>(
        AccountUiState()
    )

    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()



}
