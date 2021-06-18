package ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.volunteers

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.fragment_tabbed_volunteers.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import ru.kireev.mir.registrarlizaalert.BuildConfig
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.database.GroupCallsigns
import ru.kireev.mir.registrarlizaalert.data.database.entity.Group
import ru.kireev.mir.registrarlizaalert.presentation.extention.getGroupCallsignAsString
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.GroupsViewModel
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.MainViewModel
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.groups.GroupSectionsPagerAdapter
import java.io.File

class TabbedVolunteersFragment : Fragment(R.layout.fragment_tabbed_volunteers) {

    private val mainViewModel: MainViewModel by inject()
    private val groupsViewModel: GroupsViewModel by inject()
    private val volunteersViewModel: VolunteersViewModel by inject()

    private var pathToFile = ""
    private var allGroups = listOf<Group>()

    companion object {
        private const val REQUEST_CODE_GET_PATH_TO_FILE = 111
        private const val SPACE_KEY = " "
        private const val LINE_SEPARATOR = "\n"
        private const val NOTE_SEPARATOR = "\n * * * * * * * * * * * * * *\n"

        fun newInstance() =
            TabbedVolunteersFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (main_toolbar as Toolbar).inflateMenu(R.menu.main_menu)
        setupTabbedAdapterToViewPager()
        val toggle = ActionBarDrawerToggle(
            activity,
            main_drawer_layout,
            main_toolbar as Toolbar,
            R.string.nav_open_drawer,
            R.string.nav_close_drawer
        )
        main_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view_main.setNavigationItemSelectedListener(::onSlideNavigationItemSelected)
        mainViewModel.deleteOldTable()
        groupsViewModel.allGroups.observe(viewLifecycleOwner, {
            allGroups = it
        })
    }

    private fun setupTabbedAdapterToViewPager(callsigns: GroupCallsigns? = null) {
        val adapter = callsigns?.let {
            GroupSectionsPagerAdapter(
                groupCallsign = it,
                context = requireContext(),
                fm = childFragmentManager
            )
        } ?: VolunteersSectionsPagerAdapter(
            context = requireContext(),
            fm = childFragmentManager
        )

        main_view_pager.adapter = adapter
        main_tabs.setupWithViewPager(main_view_pager)
    }

    private fun onSlideNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_group_volunteers -> setupTabbedAdapterToViewPager()
            R.id.nav_group_lisa -> setupTabbedAdapterToViewPager(GroupCallsigns.LISA)
            R.id.nav_group_bort -> setupTabbedAdapterToViewPager(GroupCallsigns.BORT)
            R.id.nav_group_kinolog -> setupTabbedAdapterToViewPager(GroupCallsigns.KINOLOG)
            R.id.nav_group_veter -> setupTabbedAdapterToViewPager(GroupCallsigns.VETER)
            R.id.nav_group_voda -> setupTabbedAdapterToViewPager(GroupCallsigns.VODA)
            R.id.nav_group_pegas -> setupTabbedAdapterToViewPager(GroupCallsigns.PEGAS)
            R.id.nav_group_police -> setupTabbedAdapterToViewPager(GroupCallsigns.POLICE)
            R.id.nav_group_mchs -> setupTabbedAdapterToViewPager(GroupCallsigns.MCHS)
            R.id.nav_group_pso -> setupTabbedAdapterToViewPager(GroupCallsigns.PSO)
            R.id.nav_group_backup_db -> getPermissionStorage(R.id.nav_group_backup_db)
            R.id.nav_group_restore_db -> getPermissionStorage(R.id.nav_group_restore_db)
            R.id.nav_group_sent_info -> onClickSendGroupInfo()
        }
        main_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getPermissionStorage(menuId: Int) {

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                when (menuId) {
                    R.id.nav_group_backup_db -> backupDB()
                    R.id.nav_group_restore_db -> restoreDB()
                    else -> backupDB()
                }
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.denied_message))
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun restoreDB() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/json"
        startActivityForResult(intent, REQUEST_CODE_GET_PATH_TO_FILE)
    }

    private fun backupDB() {
        requireActivity().getExternalFilesDir(null)?.absolutePath?.let {
            pathToFile = mainViewModel.backupDatabase(it)
            setChooserIntent(pathToFile)
        }
    }

    private fun setChooserIntent(pathToFile: String) {
        val intent = Intent(Intent.ACTION_SEND)
        val file = File(pathToFile)
        val newUri =
            FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
        intent.putExtra(Intent.EXTRA_STREAM, newUri)
        intent.type = "application/json"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val chooser = Intent.createChooser(intent, getString(R.string.chooser_title))
        startActivity(chooser)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    private fun onClickSendGroupInfo() {
        val builder = StringBuilder()
        for (group in allGroups) {
            if (group.archived == "true") {
                builder
                    .append(getString(R.string.archived))
                    .append(SPACE_KEY)
            }
            builder
                .append(group.groupCallsign.getGroupCallsignAsString(requireContext()))
                .append(SPACE_KEY)
                .append(group.numberOfGroup)
                .append(LINE_SEPARATOR)

                .append(getString(R.string.elder_of_group))
                .append(LINE_SEPARATOR)
            runBlocking {
                val job = launch {
                    val elder = volunteersViewModel.getVolunteerById(group.elderOfGroupId)
                    builder.append(elder.fullName)
                        .append(LINE_SEPARATOR)
                        .append(elder.phoneNumber)
                        .append(LINE_SEPARATOR)
                        .append(LINE_SEPARATOR)
                }
                job.join()
            }

            builder.append(getString(R.string.searchers))
                .append(LINE_SEPARATOR)

            runBlocking {
                val job = launch {
                    val volunteers = if (group.archived == "true") {
                        volunteersViewModel.getVolunteersByIdOfArchiveGroup(group.id)
                    } else {
                        volunteersViewModel.getVolunteersByIdOfGroup(group.id)
                    }
                    for (volunteer in volunteers) {
                        if (volunteer.uniqueId != group.elderOfGroupId) {
                            builder
                                .append(volunteer.fullName)
                                .append(LINE_SEPARATOR)
                                .append(volunteer.phoneNumber)
                                .append(LINE_SEPARATOR)
                        }
                    }
                }
                job.join()
            }


            builder
                .append(LINE_SEPARATOR)

                .append(getString(R.string.label_group_detail_task))
                .append(LINE_SEPARATOR)
                .append(group.task)
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)

                .append(getString(R.string.label_detail_group_navigators))
                .append(LINE_SEPARATOR)
                .append(group.navigators)
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)

                .append(getString(R.string.label_detail_group_compasses))
                .append(LINE_SEPARATOR)
                .append(group.compasses)
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)

                .append(getString(R.string.label_detail_group_walkie_talkies))
                .append(LINE_SEPARATOR)
                .append(group.walkieTalkies)
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)

                .append(getString(R.string.label_detail_group_lamps))
                .append(LINE_SEPARATOR)
                .append(group.lamps)
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)

                .append(getString(R.string.label_detail_group_other))
                .append(LINE_SEPARATOR)
                .append(group.others)
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR)

                .append(NOTE_SEPARATOR)
        }

        val message = builder.toString()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        val chosenIntent = Intent.createChooser(intent, getString(R.string.chooser_title))
        startActivity(chosenIntent)
    }
}