# Ears Hardfork Backport
Partial backport of the improved skinfix + Ears impl that is likely to be included in the upcomming CE 3.x releases for minecraft 1.6.4

## Limitations:
- Must be installed as a coremod (in the folder `.minecraft/coremods/`) due to needing to replace a vanilla class
- As mentioned above, this addon replaces the vanilla class `ThreadDownloadImage`; any other addon that makes changes to this class will likely crash
- Currently (as of v1.0) the default player skin does not work correctly. This should be fixed in an upcomming release.

This mod, like upstream Ears, is licensed under the MIT License.
