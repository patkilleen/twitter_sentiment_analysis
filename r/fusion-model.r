library("kappalab")#
args <- commandArgs(TRUE)
fusionTrainingDatasetPath <- args[1]
fusionTestDataDatasetPath <- args[2]
resultFilePath <- args[3]
alphaParam <- args[4]

#read the fuision training dataset
#fusion dataset format: pi(j),pi(j+1),...,pi(j+n),realtag
#where pij is the prediction of the ith sample made by model j
print(paste0("reading fusion training set from ",fusionTrainingDatasetPath));
fusionTrainingDataset = read.csv(file=fusionTrainingDatasetPath,header=FALSE,sep=",")

print("read the training fusion file");
#get number of models (-1 since real tag is a column in the fusion training dataset)
numModels = ncol(fusionTrainingDataset) -1 

print(paste0("number of modesl: ", numModels));

print("training dataset")

realTagColIx = ncol(fusionTrainingDataset)

print(paste0("column ix for real tag: ",realTagColIx))
print("creating real tag list");

#extract the real tags column as vector
realTrainingTags = fusionTrainingDataset[,realTagColIx]
print(head(realTrainingTags))
print("creating prediction matrix");
#this assumes there is atleast one model prediction in fusion isntance
#fusionTrainingInstanceMatrix = cbind(fusionTrainingDataset[,1])
fusionTrainingInstanceMatrix = data.matrix(cbind(fusionTrainingDataset[,1:(realTagColIx-1)]))

print("created prediction matrix");
print(head(fusionTrainingInstanceMatrix))
#put all the fusion instance predictions in to a matrix
#with format as		pj	pj+1	pn
#				i
#				i+1
#				i+2
# where pj is jth model, and i is the ith sample 

#for(i in 2:numModels){
#	fusionTrainingInstanceMatrix = cbind(fusionTrainingDataset[,i])
#}


#give equal weight/relavence to each model (number of models = number of columns)
mu.unif = as.capacity(uniform.capacity(numModels))

print("training using HLMS...");
print(paste0("alpha value: ",alphaParam))
#run the HLMS algorithm to train fusion model
hls = heuristic.ls.capa.ident(numModels,mu.unif,fusionTrainingInstanceMatrix,realTrainingTags,alpha=as.numeric(alphaParam))#alpha=0.05

#now we trained our fusion model, we can feed test data to fusion model using Choquet integral on trained model/coeficient estiation
#iterate the testing data, predict label for each sample and output the results to a file
#> testTweet = c(Pos,Pos,Neg,Neg)
#Choquet.integral(hls$solution,testTweet)

#read the test dataset (sep="" indicates split by whitespace, tab, and carriage return)
#fusion test dataset format: pi(j),pi(j+1),...,pi(j+n),realtag
#where pij is the prediction of the ith sample made by model j
print(paste0("reading fusion test set from ",fusionTestDataDatasetPath));
fusionTestingDataset = read.csv(file=fusionTestDataDatasetPath,header=FALSE,sep=",")

realTagColIx = ncol(fusionTestingDataset)
#extract the real tags column as vector
realTestTags = fusionTestingDataset[,realTagColIx]

#this assumes there is atleast one model prediction in fusion isntance
#fusionTestingInstanceMatrix = cbind(fusionTestingDataset[,1])

#put all the fusion instance predictions in to a matrix
#with format as		pj	pj+1	pn
#				i
#				i+1
#				i+2
# where pj is jth model, and i is the ith sample 

#for(i in 2:numModels){
#	fusionTestingInstanceMatrix = cbind(fusionTestingDataset,fusionTestingDataset[,i])
#}
fusionTestingInstanceMatrix = as.matrix(fusionTestingDataset[,1:(realTagColIx-1)])

print(paste0("writing fusion prediction results to ",resultFilePath));
for(sampleIx in 1:nrow(fusionTestingInstanceMatrix)) {
	testTweet=fusionTestingInstanceMatrix[sampleIx,]
	realTag=realTestTags[sampleIx]


	#make a prediction using fusion model
	prediction = Choquet.integral(hls$solution,testTweet)
	#make line of form: prediction,real-tag
	#to append to output result file
	outputLine = paste0(prediction,",",realTag)
	write(outputLine,file=resultFilePath,append=TRUE)
    
}
print("done")