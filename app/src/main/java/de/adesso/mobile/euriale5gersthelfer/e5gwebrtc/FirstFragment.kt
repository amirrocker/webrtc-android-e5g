package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.databinding.FragmentFirstBinding
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp.Wamper

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var wamp: WampFlags? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
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

    private fun setupWamp() = Wamper(requireActivity(), "abcdef")
}
