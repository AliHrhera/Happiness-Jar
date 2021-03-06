package era.apps.happinessjar.ui.view

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import era.apps.happinessjar.R
import era.apps.happinessjar.data.networking.AppApi
import era.apps.happinessjar.databinding.ActivityMainBinding
import era.apps.happinessjar.ui.viewmodel.ChatViewModel
import era.apps.happinessjar.ui.viewmodel.MessagesViewModel
import era.apps.happinessjar.ui.viewmodel.StoriesViewModel
import era.apps.happinessjar.util.DataManger
import era.apps.happinessjar.util.callback.OnItemClick


class MainActivity : AppCompatActivity() {

    private lateinit var acBar: ActionBar
    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding

    private lateinit var messageViwModel: MessagesViewModel
    lateinit var storyViewModel: StoriesViewModel
    lateinit var chatModel: ChatViewModel

    fun showBottom() {
        binding.bLay.parentView.visibility = View.VISIBLE
    }

    fun hidBottom() {
        binding.bLay.parentView.visibility = View.GONE
    }

    private lateinit var messages: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DataManger.getInstance().setAppStatues(status)
        DataManger.getInstance().normal()
        messageViwModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(MessagesViewModel::class.java)
        storyViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(StoriesViewModel::class.java)
        chatModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(ChatViewModel::class.java)

        if (supportActionBar != null) {
            acBar = supportActionBar!!
        }

        val setting = findViewById<RelativeLayout>(R.id.bottom_settings)
        val stories = findViewById<RelativeLayout>(R.id.bottom_stories)
        val like = findViewById<RelativeLayout>(R.id.bottom_like)
        val whatsApp = findViewById<RelativeLayout>(R.id.bottom_whatsApp)
        messages = findViewById(R.id.bottom_message)

        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        container = findViewById(R.id.container)

        //bottomDefaultViewStatus()
        setting.setOnClickListener {
            run {
                bottomDefaultViewStatus(it as RelativeLayout)
                attachFragment(R.id.settingsFragment)
            }

        }
        stories.setOnClickListener {
            run {
                bottomDefaultViewStatus(it as RelativeLayout)
                attachFragment(R.id.navStoriesFragment)

            }
        }
        like.setOnClickListener {
            run {
                bottomDefaultViewStatus(it as RelativeLayout)
                attachFragment(R.id.navAllLikedMessagesFragment)

            }
        }
        whatsApp.setOnClickListener {
            run {
                bottomDefaultViewStatus(it as RelativeLayout)
                attachFragment(R.id.navWhatsAppMessagesFragment)

            }
        }
        messages.setOnClickListener {
            run {
                bottomDefaultViewStatus(it as RelativeLayout)
                attachFragment(R.id.navMessagesFragment)

            }
        }
        val fcm = getSharedPreferences("info", 0)
                .getString("fcm", "").toString()
        val myRef = FirebaseDatabase.getInstance().getReference("DeviceTokens/$fcm")
        AppApi.getInstance().insertNewUser(fcm)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    return
                }
                myRef.child(fcm).setValue(fcm)
                AppApi.getInstance()
                        .notifyUserThereIsNewMessage(fcm, "ربنا معاك وبيجبر بخاطرك ديما و بيحبك ف تفائل ❤️", "message")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        if (intent.action != null) {
            val type = intent.action.toString()
            if (type.contains("chat")) {
                attachFragment(R.id.chatFragment)
            } else if (type.contains("message")) {
                attachFragment(R.id.navMessagesFragment)
            }
        }


    }

    private var fragID = 0
    fun attachFragment(fragId: Int) {
        if (fragID != fragId) {
            navController.navigate(fragId)
            fragID = fragId
        }
    }

    fun getMessageViwModel(): MessagesViewModel {
        return messageViwModel
    }


    fun hidActionBar() {
        this.acBar.hide()
    }

    private lateinit var container: LinearLayout
    private var crId = 0
    private fun bottomDefaultViewStatus(view: RelativeLayout) {
        if (crId != view.id) {
            val h = view.height
            var pram = LinearLayout.LayoutParams(0, h)
            for (index in 0 until (container).childCount) {
                val nextChild: RelativeLayout = ((container).getChildAt(index) as RelativeLayout)
                nextChild.setPadding(0, 0, 0, 0)
                pram.weight = 1f
                nextChild.layoutParams = pram
            }
            pram = LinearLayout.LayoutParams(0, view.height - 10)
            view.setPadding(0, 0, 0, 5)
            view.elevation = 10f
            pram.weight = 1f
            view.layoutParams = pram
            crId = view.id
        }

    }


    private val status = object : OnItemClick {

        override fun onClick(item: Any) {
            if (item as Boolean) {
                binding.loading.visibility = View.VISIBLE

                return
            }
            binding.loading.visibility = View.GONE

        }
    }

    override fun onBackPressed() {
        if (binding.loading.visibility == View.VISIBLE) {
            binding.loading.visibility = View.GONE
            return
        }
        super.onBackPressed()
    }


    fun showNameDialog() {
        object : Dialog(this@MainActivity) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.dialog_get_name)
                val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
//                    if (window == null) {
//                        return
//                    }
//                    // getWindow().setBackgroundDrawableResource(R.color.transparent);
                window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                window?.setBackgroundDrawableResource(R.color.fullTrans)
                val getName = findViewById<EditText>(R.id.getName)
                findViewById<View>(R.id.done).setOnClickListener {
                    val name = getName.text.toString()
                    getSharedPreferences("info", 0)?.edit()
                            ?.putString("messageName", name)?.apply()
                    getSharedPreferences("info", 0)?.edit()
                            ?.putBoolean("showGetName", true)?.apply()
                    dismiss()
                }


            }
        }.show()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }


}