## Testing resources

Product managers, user experience designers, and others may need to test out CiviForm to see how the product currently looks and works. Recall that there are 4 user types:

* Applicants
* Trusted intermediaries (TIs)
* CiviForm admins
* Program admins

Logging in as applicants, CiviForm admins, or Program admins is pretty easy -- there should be buttons for those directly on [staging.seattle.civiform.com](https://staging.seattle.civiform.com):

* Applicants: choose "Continue as guest"
* CiviForm admins: choose "Global admin"
* Program admins: choose "Admin of all programs"

However, logging in as a TI is harder. Here's how to get in as a TI:

1. Log in as a CiviForm admin ("Global admin").
1. Go to the Intermediaries tab.
1. Edit the "group name" intermediary group. (It's the only "Edit" button you'll see.)
1. Add `sherlock123@dispostable.com` as a member. You should see "OK" appear in the Status column.
1. Log out.
1. Log in with IDCS as a Trusted Intermediary. Use `sherlock` as the username and `221Bbaker!` (including the exclamation point) as the password.
1. You should be in! Look for a link to the Trusted Intermediary Dashboard.

If this Sherlock test account isn't found, you'll need to create a new IDCS account:

1. Log in with IDCS. Hit "Register" to make a new account.
1. Create a new throwaway email address on [dispostable.com](https://dispostable.com). 
1. Create an account with that email address. Remember the username and password, and update this Wiki page accordingly.