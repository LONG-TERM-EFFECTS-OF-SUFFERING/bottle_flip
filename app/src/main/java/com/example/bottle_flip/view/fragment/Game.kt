package com.example.bottle_flip.view.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.os.CountDownTimer
import com.example.bottle_flip.R
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.bottle_flip.Dialogs.ChallengeDialog
import com.example.bottle_flip.databinding.GameBinding
import com.example.bottle_flip.repository.challengeRepository
import com.example.bottle_flip.model.Challenge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Game : Fragment() {

    private lateinit var countdownText: TextView    //texto contador
    private lateinit var countdownTimer: CountDownTimer //Tiempo contador
    private var backgroundMediaPlayer: MediaPlayer? = null
    private var effectMediaPlayer: MediaPlayer? = null // Para el sonido de efecto
    private lateinit var bottleImage: ImageView
    private lateinit var btnSpin: LottieAnimationView
    private var lastAngle = 0f // Variable para almacenar el último ángulo de la botella

    private lateinit var navController: NavController
    private lateinit var audioBackground: MediaPlayer
    private lateinit var audioSpinBottle: MediaPlayer
    private lateinit var binding: GameBinding  //Acceder a los componenetes de la vista principal
    private var isMute: Boolean = true
    private lateinit var challengeRepository: challengeRepository

    private val _statusShowDialog = MutableLiveData(false)
    val statusShowDialog: LiveData<Boolean> get() = _statusShowDialog

    private val _statusRotationBottle = MutableLiveData(false)
    val statusRotationBottle: LiveData<Boolean> get() = _statusRotationBottle

    private val _rotationBotle = MutableLiveData<RotateAnimation>()
    val rotationBottle: LiveData<RotateAnimation> get() = _rotationBotle

    private val _enableButton = MutableLiveData(true)
    val enableButton: LiveData<Boolean> get() = _enableButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GameBinding.inflate(inflater)
        binding.lifecycleOwner = this
        challengeRepository = challengeRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menu(view) //Manejar el menu
        media()    //Manejar el sonido
        bottle()   //Manejar la botella
    }

    //XXXXXXXXXXXXXXXXXXXXXXXXXX CONTROL XXXXXXXXXXXXXXXXXXXXXXXXXXX
    // Funciones del menu
    private fun menu(view: View) {
        navController = Navigation.findNavController(view)
        //Calificar app
        binding.icMenu.btnStarBorder.setOnClickListener {
            val appPackageName = "com.nequi.MobileApp" // Replace with your app's package name
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }
        //Como se juega
        binding.icMenu.btnGame.setOnClickListener {
            audioBackground.pause()
            findNavController().navigate(R.id.action_game_to_instructions)
            statusShowDialog(false)
        }
        //Audio ON/OFF
        binding.icMenu.btnVolumeOn.setOnClickListener {
            if (isMute) {
                // Pausar la reproducción y actualizar el ícono a "apagado"
                audioBackground.pause()
                binding.icMenu.btnVolumeOn.setImageResource(R.drawable.ic_volume_off_30)
                isMute= false
            } else {
                // Reanudar la reproducción y actualizar el ícono a "encendido"
                audioBackground.start()
                binding.icMenu.btnVolumeOn.setImageResource(R.drawable.ic_volume_on_30)
                isMute= true
            }
        }
        //Consultar y mostrar reto
        binding.icMenu.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_game_to_challenges)
            statusShowDialog(false)
        }
        //Compartir app
        binding.icMenu.btnShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND

                val appTitle = "App pico botella"
                val slogan = "Solo los valientes lo juegan !!"
                val downloadUrl = "https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es"
                val message = "$appTitle\n$slogan\n$downloadUrl"

                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share app via")
            it.context.startActivity(shareIntent)
        }

        //Girar botella
        binding.btnSpin.setOnClickListener {
            spinBottle()
        }
    }

    // Funciones de interacccion con el audio
    private fun media() {
        audioBackground = MediaPlayer.create(context, R.raw.background_audio)
        audioSpinBottle = MediaPlayer.create(context, R.raw.audio_bottle)
        audioBackground.start()
    }

    private fun bottle() {
        bottleRotation()        //Movimiento botella
        bottleButton()          //Boton botella
        bottleChallenge()       //Mostrar reto
    }

    //XXXXXXXXXXXXXXXXXXXXXXX FUNCIONES BOTELLA XXXXXXXXXXXXXXXXXXXXXXXXXXXX
    private fun bottleChallenge() {
        statusShowDialog.observe(viewLifecycleOwner) {
            if (it) {//Implementar consultar reto
                //runBlocking {//Implementar contador
                  //  delay(3 * 1000L)
                //}
                //showCountdown()
                //audioSuspense.pause()
               // val messageChallenge = "Debes tomar un trago"
              //  dialogShowChallenge(
               //     requireContext(),
               //     audioBackground, isMute,
              //      messageChallenge
               // )
                //audioShowChallenge.start()
                audioSpinBottle.pause()
                showCountdown()
                audioBackground.start()
                //audioButton.pause()
            }
       }
    }

    private fun bottleButton() {
        enableButton.observe(viewLifecycleOwner) { enableButton ->
            binding.btnSpin.isVisible = enableButton
        }
    }

    private fun bottleRotation() {
        statusRotationBottle.observe(viewLifecycleOwner) { statusRotation ->
            if (statusRotation) {
                //audioButton.start()
                audioBackground.pause()
                audioSpinBottle.start()
                rotationBottle.observe(viewLifecycleOwner) { rotation ->
                    binding.centerImageView.startAnimation(rotation)
                }
            }
        }
    }

    // Funciones
    private fun spinBottle() {  //Girar botella
        //countdownText.visibility = View.GONE    //Ocultar contador al girar la botella
        _statusRotationBottle.value = true

        val degrees = (Math.random() * 3600) + 1080
        val rotation = RotateAnimation(
            0f, degrees.toFloat(),
            Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotation.fillAfter = true
        rotation.duration = 3600
        rotation.interpolator = DecelerateInterpolator()

        rotation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                //_enabledStreamers.value = true
                _enableButton.value = false
            }

            override fun onAnimationEnd(p0: Animation?) {
                _statusRotationBottle.value = false
                _enableButton.value = true
               // _enabledStreamers.value = false
                _statusShowDialog.value = true
            }
            override fun onAnimationRepeat(p0: Animation?) {
            }
        })
        _rotationBotle.value = rotation
    }

//    private fun showCountdown() {//Mostrar y ocultar contador
//        binding.countdownText.visibility = View.VISIBLE // Hacer visible el contador
//        binding.countdownText.text = "3" // Comenzar con 3
//
//        //deshabilitar el boton mientras esta activo el contador
//        binding.btnSpin.isEnabled = false
//
//         object : CountDownTimer(4000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val secondsRemaining = (millisUntilFinished / 1000).toInt()
//                binding.countdownText.text =
//                    secondsRemaining.toString() // Actualiza el texto con el tiempo restante
//            }
//
//            override fun onFinish() {
//                binding.countdownText.text = "0" // Muestra 0 al final
//                binding.countdownText.visibility = View.GONE
//                binding.btnSpin.isEnabled = true
////                showChallengeDialog()
//
////                _statusShowDialog.value = true
//            }
//        }.start() // Inicia el temporizador
//    }

    private fun showCountdown() {
        binding.countdownText.visibility = View.VISIBLE
        binding.countdownText.text = "3"
        binding.btnSpin.isEnabled = false

        object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.countdownText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                binding.countdownText.visibility = View.GONE
                binding.btnSpin.isEnabled = true
                loadChallenge()
            }
        }.start()
    }

    private fun loadChallenge() {
        lifecycleScope.launch {
            val challenge = getRandomChallengeFromDatabase()
            if (challenge != null) {
                ChallengeDialog.showDialogChallenge(requireContext(), this@Game, challenge.description)
            } else {
                ChallengeDialog.showDialogChallenge(requireContext(), this@Game, "No hay retos disponibles")
            }
        }
    }

    private suspend fun getRandomChallengeFromDatabase(): Challenge? {
        return withContext(Dispatchers.IO) {
            val challenges = challengeRepository.getListChallenge()
            if (challenges.isNotEmpty()) challenges.random() else null
        }
    }

    fun statusShowDialog(status: Boolean) {
        _statusShowDialog.value = status
    }

    fun dialogShowChallenge(
        context: Context,
        audioBackground: MediaPlayer,
        isMute: Boolean,
        messageChallenge: String,
    ) {
       // showDialogChallenge(context, audioBackground, isMute, messageChallenge)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioBackground.stop()
    }

    override fun onPause() {
        super.onPause()
        audioBackground.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!isMute) {
            audioBackground.start()
        }
    }

}
