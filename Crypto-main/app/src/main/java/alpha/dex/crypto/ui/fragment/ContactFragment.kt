// ui/fragment/ContactFragment.kt
package alpha.dex.crypto.ui.fragment

import alpha.dex.crypto.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.facebookIcon).setOnClickListener {
            // Handle Facebook icon click
        }
        view.findViewById<View>(R.id.twitterIcon).setOnClickListener {
            // Handle Twitter icon click
        }
        view.findViewById<View>(R.id.linkedinIcon).setOnClickListener {
            // Handle LinkedIn icon click
        }
        view.findViewById<View>(R.id.instagramIcon).setOnClickListener {
            // Handle Instagram icon click
        }
    }
}
