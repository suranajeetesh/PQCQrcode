package com.pqc.qr.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * Created by Jeetesh Surana.
 */
@ActivityRetainedScoped
class HomeRepository @Inject constructor(@ApplicationContext context: Context)