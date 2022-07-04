package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.AppPermissions.REQUEST_CODE_CAMERA_PERMISSION
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.databinding.FragmentFirstBinding
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp.Wamper
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc.BasicWebRtC

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var wamper: Wamper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        wamper = Wamper(requireActivity(), "abcdef").apply {
            setupWamp()
        }

        checkPermission()
    }

    private fun checkPermission() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                permissions,
                REQUEST_CODE_CAMERA_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            activity?.let { BasicWebRtC.setup(it, eglBase = wamper.eglBase) }

            // show local view
            val localStream = BasicWebRtC.localStream
            val renderer = binding.svrLocalRenderView
            val localRenderer = wamper.setupRenderer(renderer)
            localStream!!.videoTracks.first.addRenderer(localRenderer)
            wamper.connect()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private val connectionList: List<Connection> = emptyList()

//    private val userId = UUID.randomUUID().toString().substring(0, 8)
//
//    // EglBase seems to handle all Surface related operations.
//    private val eglBase = EglBase.create()

//    private fun setupWamp() = Wamper(requireActivity(), "abcdef")
}
