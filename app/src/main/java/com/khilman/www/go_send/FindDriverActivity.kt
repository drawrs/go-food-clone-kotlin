package com.khilman.www.go_send

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_find_driver.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class FindDriverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_driver)

        btnCancelOrder.onClick {
            cancelDialog()
        }

        btnNewOrder.onClick {
            startActivity(intentFor<MainActivity>().singleTop())
        }
    }

    private fun cancelDialog() {
        alert {
            title = "Confirmation"
            message = "Are you sure want to Cancel this Order ?"

            yesButton {
                title = "Yes"
                startActivity(intentFor<CancelReasonActivity>().clearTask())
            }
            noButton {
                title = "Close"
            }
        }.show()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        cancelDialog()
    }
}
