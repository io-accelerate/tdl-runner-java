# How to update and release new client library

Change library version is:
- `build.gradle`
- `pom.xml`

Runner version = Client version + Runner patch
Example:
```
client version = 0.24.0
patch version = 0
runner version = 0.24.0.0
```

Update the runner version (0.X.Y.Z) in:
- `version.txt`


## To release

Commit all changes then:

```bash
git tag -a "v$(cat version.txt)"
git push --tags
git push
```