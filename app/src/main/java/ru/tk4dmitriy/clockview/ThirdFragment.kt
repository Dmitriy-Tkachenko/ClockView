package ru.tk4dmitriy.clockview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.tk4dmitriy.clockview.databinding.FragmentThirdBinding

class ThirdFragment : Fragment(R.layout.fragment_third) {
    private lateinit var binding: FragmentThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThirdBinding.inflate(inflater, container, false)
        binding.clockViewTwo.frameColor = ContextCompat.getColor(requireActivity(), R.color.brown)
        binding.clockViewTwo.clockMarkersColor = ContextCompat.getColor(requireActivity(), R.color.brown)
        binding.clockViewTwo.hourLabelsColor = ContextCompat.getColor(requireActivity(), R.color.brown)
        binding.clockViewTwo.hourHandColor = ContextCompat.getColor(requireActivity(), R.color.brown)
        binding.clockViewTwo.minuteHandColor = ContextCompat.getColor(requireActivity(), R.color.brown)
        binding.clockViewTwo.secondHandColor = ContextCompat.getColor(requireActivity(), R.color.red)
        binding.clockViewTwo.clockMarkersColor = ContextCompat.getColor(requireActivity(), R.color.brown)

        binding.clockViewThree.frameColor = ContextCompat.getColor(requireActivity(), R.color.green)
        binding.clockViewThree.clockMarkersColor = ContextCompat.getColor(requireActivity(), R.color.green)
        binding.clockViewThree.hourLabelsColor = ContextCompat.getColor(requireActivity(), R.color.green)
        binding.clockViewThree.hourHandColor = ContextCompat.getColor(requireActivity(), R.color.green)
        binding.clockViewThree.minuteHandColor = ContextCompat.getColor(requireActivity(), R.color.green)
        binding.clockViewThree.secondHandColor = ContextCompat.getColor(requireActivity(), R.color.black)
        binding.clockViewThree.clockMarkersColor = ContextCompat.getColor(requireActivity(), R.color.green)

        binding.clockViewFour.frameColor = ContextCompat.getColor(requireActivity(), R.color.blue)
        binding.clockViewFour.clockMarkersColor = ContextCompat.getColor(requireActivity(), R.color.blue)
        binding.clockViewFour.hourLabelsColor = ContextCompat.getColor(requireActivity(), R.color.blue)
        binding.clockViewFour.hourHandColor = ContextCompat.getColor(requireActivity(), R.color.blue)
        binding.clockViewFour.minuteHandColor = ContextCompat.getColor(requireActivity(), R.color.blue)
        binding.clockViewFour.secondHandColor = ContextCompat.getColor(requireActivity(), R.color.brown)
        binding.clockViewFour.clockMarkersColor = ContextCompat.getColor(requireActivity(), R.color.blue)
        return binding.root
    }
}